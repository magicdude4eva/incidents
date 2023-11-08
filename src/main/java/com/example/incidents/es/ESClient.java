package com.example.incidents.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.DateProperty;
import co.elastic.clients.elasticsearch._types.mapping.GeoPointProperty;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.incidents.mapper.CommonObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Encapsulates low-level access to ElasticSearch (connection management, logging incidents and search functionality).
 */
public class ESClient {

    private final ElasticsearchClient elasticsearchClient;

    /**
     * Creates a new instance of the ESClient.
     *
     * @param host complete host URL (including scheme and port)
     * @param credentialsProvider credentials (e.g. username and password)
     * @param sslContext the SSL context in case https is supposed to be used
     */
     ESClient(HttpHost host, CredentialsProvider credentialsProvider, SSLContext sslContext) {
         final var restClient = RestClient
                .builder(host)
                .setHttpClientConfigCallback(builder -> {
                    builder.setDefaultCredentialsProvider(credentialsProvider);

                    if (Objects.nonNull(sslContext)) {
                        builder.setSSLContext(sslContext);
                    }

                    return builder;
                })
                .build();

        // Create the transport with a Jackson mapper
        final var transport = new RestClientTransport(restClient, new JacksonJsonpMapper(CommonObjectMapper.create()));

        // And create the API client
        elasticsearchClient = new ElasticsearchClient(transport);
    }

    /**
     * Helper function to check a given index for existence.
     * @param indexName the ES-index name to check
     *
     * @return true, if "indexName" exists, and false else
     */
    private boolean indexExists(@NotBlank String indexName) throws ESException {
        try {
            final var response = elasticsearchClient.indices().exists(req -> req.index(indexName));
            return response.value();
        } catch (Exception exception) {
            throw new ESException("indices.exists", indexName, exception);
        }
    }

    /**
     * Create an index to save incidents if it does not exist yet.
     *
     * @param indexName the name of the index to be created
     *
     * @throws ESException when an ES-error happens
     */
    public void createIndexForIncidents(@NotBlank String indexName) throws ESException {
        if (!indexExists(indexName)) {
            try {
                final var propertyMappings = Map.of(
                        "incidentType", Property.of(p -> p.keyword(KeywordProperty.of(k -> k))),
                        "location", Property.of(p -> p.geoPoint(GeoPointProperty.of(g -> g))),
                        "timestamp", Property.of(p -> p.date(DateProperty.of(d -> d))),
                        "severityLevel", Property.of(p -> p.keyword(KeywordProperty.of(k -> k))));

                elasticsearchClient.indices().create(
                        c -> c.index(indexName)
                                .settings(s -> s.numberOfShards("1").numberOfReplicas("1"))
                                .mappings(m -> m.properties(propertyMappings)));
            } catch (Exception exception) {
                throw new ESException("indices.create", indexName, exception);
            }
        }
    }

    /**
     * Delete the given index if it exists.
     *
     * @param indexName the name of the ES-index to be deleted
     *
     * @throws ESException when an ES-error happens
     */
    public void deleteIndex(@NotBlank String indexName) throws ESException {
        if (indexExists(indexName)) {
            try {
                elasticsearchClient.indices().delete(d -> d.index(indexName));
            } catch (Exception exception) {
                throw new ESException("indices.delete", indexName, exception);
            }
        }
    }

    /**
     * Flushing an index is the process of making sure, that any data that is currently only stored in the
     * transaction log is also permanently stored in the Lucene index.
     *
     * @param indexName ES-index to be flushed or null, if all indices are supposed to be flushed
     *
     * @throws ESException when flushing fails
     */
    public void flushIndex(@NotBlank String indexName) throws ESException {
        try {
            elasticsearchClient.indices().flush(req -> req.force(true).waitIfOngoing(true));
        } catch (Exception exception) {
            throw new ESException("indices.flush", indexName, exception);
        }
    }

    /**
     * Refresh makes all operations performed on one or more indices since the last refresh available for search.
     *
     * @param indexName ES-index to be refreshed, or null is all indices are supposed be refreshed
     *
     * @throws ESException when refreshing fails
     */
    public void refreshIndex(@NotBlank String indexName) throws ESException {
        try {
            elasticsearchClient.indices().refresh(req -> req);
        } catch (Exception exception) {
            throw new ESException("indices.refresh", indexName, exception);
        }
    }

    /**
     * Returns the number of documents in the given index name.
     * If the index does not exist, the result of this function is 0.
     *
     * @param indexName ES-index to be counted
     *
     * @return number of documents in "indexName"
     *
     * @throws ESException if counting fails
     */
    public long countDocuments(@NotBlank String indexName) throws ESException {
        if (indexExists(indexName)) {
            try {
                final var response = elasticsearchClient.count(esIdx -> esIdx.index(indexName));
                return response.count();
            } catch (Exception exception) {
                throw new ESException("count", indexName, exception);
            }
        } else {
            return 0L;
        }
    }

    /**
     * Saves the provided "incident" to ES. Returns an instance of "IndexResponse".
     *
     * @param indexName ES-index name to use for saving
     * @param incident incident instance
     *
     * @return "IndexResponse"-instance holding the created incident's UUID in ES and the version.
     *
     * @throws ESException if saving to ES fails
     */
    public IndexResponse logIncident(@NotBlank String indexName,
                                     @NotNull Incident incident) throws ESException {

        try {
            final var response = elasticsearchClient.index(esIdx -> esIdx
                    .index(indexName)
                    .id(incident.id().toString())
                    .document(incident));

            return new IndexResponse(incident.id(), response.version());
        } catch (Exception exception) {
            throw new ESException("index", indexName, exception);
        }
    }

    public SearchResult performSearch(@NotNull SearchRequest searchRequest) throws ESException {
        try {
            final var response = elasticsearchClient.search(searchRequest, Incident.class);

            final var totalHits = Optional.ofNullable(response.hits().total());
            final var resultCount = response.hits().hits().size();
            final var resultSet = response.hits().hits()
                    .stream()
                    .map(Hit::source)
                    .toList();

            return new SearchResult(totalHits.map(TotalHits::value).orElse(0L), resultCount, resultSet);
        } catch (Exception exception) {
            throw new ESException("search", String.join(", ", searchRequest.index()), exception);
        }
    }
}