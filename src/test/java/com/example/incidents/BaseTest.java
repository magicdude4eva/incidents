package com.example.incidents;

import com.example.incidents.common.IncidentSeverityLevel;
import com.example.incidents.common.IncidentType;
import com.example.incidents.es.ESClientFactory;
import com.example.incidents.es.ESException;
import com.example.incidents.es.Incident;
import com.example.incidents.service.ConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Point;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contains code and data common to service- and API-tests.
 * All functions in this class use the ESClient (= lowest level) directly.
 */
public abstract class BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    @Autowired
    ConfigProperties configProperties;

    @Autowired
    ESClientFactory esClientFactory;

    /**
     * The test index will be recreated before each test. Necessary to have a clean environment.
     */
    @BeforeEach
    void prepareData() {
        try {
            recreateIndex();
        } catch (ESException esException) {
            LOGGER.error(esException, esException::getMessage);
        }
    }

    /**
     * Create incidents for each combination of incidentType and severityLevel. The location is a random value,
     * for timestamp the current UTC-time will be used.
     *
     * @return mapping from UUID => Incident for checking
     *
     * @throws ESException when a call to ES fails
     */
    protected Map<UUID, Incident> createIncidentsProduct() throws ESException {
        final HashMap<UUID, Incident> result = new HashMap<>();
        final var random = new Random();

        for (IncidentType type : IncidentType.values()) {
            for (IncidentSeverityLevel severity : IncidentSeverityLevel.values()) {
                final var geopoint = GeoPoint.fromPoint(new Point(
                        random.nextDouble(360.0) - 180.0,
                        random.nextDouble(180.0) - 90.0));

                final var id = UUID.randomUUID();
                final var timestamp = Instant.now();
                final var incident = new Incident(id, type.name(), geopoint, timestamp, severity.name());
                esClientFactory.create().logIncident(configProperties.incidentsIndexName(), incident);

                result.put(id, incident);
            }
        }

        return result;
    }

    /**
     * Delete the test index and recreate it to have a clean environment before each test.
     *
     * @throws ESException when a call to ES fails
     */
    protected void recreateIndex() throws ESException {
        esClientFactory.create().deleteIndex(configProperties.incidentsIndexName());
        esClientFactory.create().createIndexForIncidents(configProperties.incidentsIndexName());
    }

    /**
     * Perform a flush and a refresh on the ES-index used for tests.
     * Both are synchronous operations that will make the client wait.
     * For unit tests this is necessary.
     *
     * @throws ESException when a call to ES fails
     */
    protected void flushAndRefreshIndex() throws ESException {
        esClientFactory.create().flushIndex(configProperties.incidentsIndexName());
        esClientFactory.create().refreshIndex(configProperties.incidentsIndexName());
    }

    /**
     * Check, that the ES-index used for the test is empty.
     */
    protected void assertIndexIsEmpty() throws ESException {
        assertEquals(0L, esClientFactory
                .create()
                .countDocuments(configProperties.incidentsIndexName()));
    }

    /**
     * Count the number of elements in the test index.
     *
     * @return number of documents
     *
     * @throws ESException when a call to ES fails
     */
    protected long countDocumentsInIndex() throws ESException {
        return esClientFactory
                .create()
                .countDocuments(configProperties.incidentsIndexName());
    }
}
