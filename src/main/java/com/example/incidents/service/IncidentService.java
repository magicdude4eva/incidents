package com.example.incidents.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.GeoDistanceQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.example.incidents.data.SearchCriteria;
import com.example.incidents.data.SearchResult;
import com.example.incidents.es.ESClientFactory;
import com.example.incidents.es.ESException;
import com.example.incidents.mapper.DataToESMapper;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@EnableConfigurationProperties(ConfigProperties.class)
public class IncidentService {

    final static Logger LOGGER = LoggerFactory.getLogger(IncidentService.class);

    @Autowired
    ConfigProperties configProperties;

    @Autowired
    ESClientFactory esClientFactory;

    /**
     * Create a new entry for "dataIncident" in the ES-index defined in configuration.
     * Returns the UUID of the generated dataIncident.
     *
     * @param dataIncident the dataIncident to be saved to ES
     *
     * @return the UUID of the newly created dataIncident
     *
     * @throws TechnicalException if the request to ES failed
     */
    public UUID logIncident(@NotNull com.example.incidents.data.Incident dataIncident) throws TechnicalException {
        try {
            final var esIncident = DataToESMapper.INSTANCE.dataIncidentToES(dataIncident);
            final var response = esClientFactory
                    .create()
                    .logIncident(configProperties.incidentsIndexName(), esIncident);
            return response.id();
        } catch (ESException esException) {
            throw convertAndLogESException("Error when logging new dataIncident", esException);
        }
    }

    public SearchResult search(@NotNull SearchCriteria searchCriteria) throws TechnicalException {
        try {
            // Build the base query - uses the correct index,
            // parameters "offset" and "resultCount", and add the correct sorting (by "timestamp" descending).
            final var searchRequestBuilder = new SearchRequest.Builder();
            searchRequestBuilder
                    .index(configProperties.incidentsIndexName())
                    .from(searchCriteria.offset()).size(searchCriteria.resultCount())
                    .sort(List.of(
                            SortOptions.of(builder ->
                                    builder.field(field ->
                                            field.field("timestamp").order(SortOrder.Desc)))));

            if (searchCriteria.hasSearchCriteria()) {
                searchRequestBuilder.query(buildSearchQuery(searchCriteria));
            }

            final var searchResult = esClientFactory.create().performSearch(searchRequestBuilder.build());
            return DataToESMapper.INSTANCE.esSearchResultToData(searchResult);
        } catch (ESException esException) {
            throw convertAndLogESException("Error when searching for incidents", esException);
        }
    }

    private Query buildSearchQuery(SearchCriteria searchCriteria) {
        final var subQueries = new ArrayList<Query>();

        if (Objects.nonNull(searchCriteria.type())) {
            subQueries.add(
                    MatchQuery.of(m -> m.field("type").query(searchCriteria.type().name()))._toQuery());
        }

        if (Objects.nonNull(searchCriteria.locationAndDistance())) {
            final var searchLocation = searchCriteria.locationAndDistance().location();
            final var distanceAsString = String.format("%dkm", searchCriteria.locationAndDistance().distanceInKm());

            subQueries.add(
                    GeoDistanceQuery.of(g -> g.field("location")
                            .location(l -> l.latlon(p -> p.lat(searchLocation.lat()).lon(searchLocation.lon())))
                            .distance(distanceAsString))._toQuery());
        }

        if (Objects.nonNull(searchCriteria.timestampRange())) {
            subQueries.add(
                    RangeQuery.of(r -> r.field("timestamp")
                            .from(searchCriteria.timestampRange().minTimestamp().toString())
                            .to(searchCriteria.timestampRange().maxTimestamp().toString()))._toQuery());
        }

        if (Objects.nonNull(searchCriteria.severity())) {
            subQueries.add(
                    MatchQuery.of(m -> m.field("severity").query(searchCriteria.severity().name()))._toQuery());
        }

        return BoolQuery.of(b -> b.must(subQueries))._toQuery();
    }

    /**
     * Log the ES-exception and convert it to a technical exception.
     *
     * @param errorMsg the error message to be logged
     * @param esException the ES-exception
     * @return "TechnicalException"-instance containing "errorMsg" and "esException" as cause
     */
    private static TechnicalException convertAndLogESException(String errorMsg, ESException esException) {
        LOGGER.error(errorMsg, esException);
        return new TechnicalException(errorMsg, esException);
    }
}
