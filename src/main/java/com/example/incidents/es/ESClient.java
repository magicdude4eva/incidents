package com.example.incidents.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.text.SimpleDateFormat;
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
        final var transport = new RestClientTransport(restClient, new JacksonJsonpMapper(createObjectMapper()));

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
     * Deletes all documents of the given index in ES if index exists.
     *
     * @param indexName ES-index to be deleted
     *
     * @throws ESException when deletion fails
     */
    public void deleteDocuments(@NotBlank String indexName) throws ESException {
        if (indexExists(indexName)) {
            try {
                elasticsearchClient.deleteByQuery(d -> d.index(indexName).query(MatchAllQuery.of(m -> m)._toQuery()));
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
//            elasticsearchClient.indices().flush(req -> req.index(indexName).force(true).waitIfOngoing(true));
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
//            elasticsearchClient.indices().refresh(req -> req.index(indexName));
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
            final var resultSet = response.hits().hits()
                    .stream()
                    .map(Hit::source)
                    .toList();

            return new SearchResult(totalHits.map(TotalHits::value).orElse(0L), resultSet);
        } catch (Exception exception) {
            throw new ESException("search", String.join(", ", searchRequest.index()), exception);
        }
    }

    /**
     * Creates an instance of "ObjectMapper" necessary for mapping between data classes and json.
     * Uses the time module to handle "Instant"-instances (necessary for timestamps) correctly.
     *
     * @return correctly configured "ObjectMapper"-instance
     */
    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper().
                registerModule(new JavaTimeModule()).
                setDateFormat(new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601));
    }
}