package com.example.incidents.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.GeoDistanceQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
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

/**
 * Contains all function related to incidents.
 */
@Component
@EnableConfigurationProperties(ConfigProperties.class)
public class IncidentsService {

    final static Logger LOGGER = LoggerFactory.getLogger(IncidentsService.class);

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
     * @throws ServiceException if the request to ES failed
     */
    public UUID logIncident(@NotNull Incident dataIncident) throws ServiceException {
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

    /**
     * Search for incidents as described by "searchCriteria".
     *
     * @param searchCriteria requested filtering
     *
     * @return found incidents, total number of results, number of results in current result set
     *
     * @throws ServiceException on any error
     */
    public SearchResult search(@NotNull SearchCriteria searchCriteria) throws ServiceException {
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

    /**
     * Build an ES-query using the provided search criteria. Sorting is always done by timestamp descending.
     *
     * @param searchCriteria describes requested filtering
     *
     * @return ES-query
     *
     * @throws InvalidParameterException if "searchCriteria" contains invalid parameters
     */
    private Query buildSearchQuery(SearchCriteria searchCriteria) throws InvalidParameterException {
        final var subQueries = new ArrayList<Query>();

        if (Objects.nonNull(searchCriteria.incidentType())) {
            subQueries.add(
                    MatchQuery.of(m -> m.field("incidentType")
                            .query(searchCriteria.incidentType().name()))
                            ._toQuery());
        }

        if (Objects.nonNull(searchCriteria.locationAndDistance())) {
            final var location = searchCriteria.locationAndDistance().location();
            final var distanceAsString = String.format("%dkm", searchCriteria.locationAndDistance().distanceInKm());

            subQueries.add(
                    GeoDistanceQuery.of(g -> g.field("location")
                            .location(l -> l.latlon(p -> p.lat(location.lat()).lon(location.lon())))
                            .distance(distanceAsString))._toQuery());
        }

        if (Objects.nonNull(searchCriteria.timestampRange())) {
            final var timestampRange = searchCriteria.timestampRange();
            if (timestampRange.minTimestamp().compareTo(timestampRange.maxTimestamp()) > 0) {
                throw new InvalidParameterException("minTimestamp supposed to be less than or equal to maxtimestamp");
            }

            subQueries.add(
                    RangeQuery.of(r -> r.field("timestamp")
                            .from(timestampRange.minTimestamp().toString())
                            .to(timestampRange.maxTimestamp().toString()))._toQuery());
        }

        if (Objects.nonNull(searchCriteria.severityLevel())) {
            subQueries.add(
                    MatchQuery.of(m -> m.field("severityLevel").query(searchCriteria.severityLevel().name()))._toQuery());
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
