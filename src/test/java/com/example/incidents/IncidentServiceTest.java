package com.example.incidents;

import com.example.incidents.data.Incident;
import com.example.incidents.data.IncidentSeverity;
import com.example.incidents.data.IncidentType;
import com.example.incidents.data.Location;
import com.example.incidents.data.SearchCriteria;
import com.example.incidents.es.ESClientFactory;
import com.example.incidents.es.ESException;
import com.example.incidents.service.ConfigProperties;
import com.example.incidents.service.IncidentService;
import com.example.incidents.service.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for "IncidentService".
 */
@SpringBootTest
public class IncidentServiceTest {

    final static Logger LOGGER = LoggerFactory.getLogger(IncidentServiceTest.class);

    @Autowired
    ConfigProperties configProperties;

    @Autowired
    ESClientFactory esClientFactory;

    @Autowired
    IncidentService incidentsService;

    /**
     * All documents in the index used for testing will be deleted before each test.
     */
    @BeforeEach
    void prepareData() {
        try {
            esClientFactory.create().deleteDocuments(configProperties.incidentsIndexName());
            refreshAndFlushIndex();
        } catch (ESException esException) {
            LOGGER.error(esException, esException::getMessage);
        }
    }

    /**
     * (1) Create incidents for all combinations of "type" and "severity"
     * (2) Check, that the number of documents created matches the expected number
     */
    @Test
    void testLogIncident() {
        try {
            assertIndexIsEmpty();

            // (1) Create incidents for all combinations of "type" and "severity"
            createIncidentsProduct();
            refreshAndFlushIndex();

            // (2) Check, that the number of documents created matches the expected number
            assertEquals(
                    (long) IncidentType.values().length * IncidentSeverity.values().length,
                    esClientFactory
                            .create()
                            .countDocuments(configProperties.incidentsIndexName()));
        } catch (Exception exception) {
            LOGGER.error(exception, exception::getMessage);
            fail("Failed with unexpected exception");
        }
    }

    /**
     * (1) Create incidents for all combinations of "type" and "severity"
     * (2) Search without search criteria and check, that all entries have been found and are correctly sorted.
     */
    @Test
    void testSearchAll() {
        try {
            assertIndexIsEmpty();

            // (1) Create incidents for all combinations of "type" and "severity"
            final var createdIncidentsMap = createIncidentsProduct();
            refreshAndFlushIndex();

            // (2) Search without search criteria and check, that all entries have been found and are correctly sorted.
            final var searchResult = incidentsService.search(
                    new SearchCriteria(0, 10000,
                            null, null, null, null));

            assertEquals(createdIncidentsMap.size(), searchResult.resultSet().size());
            assertCorrectSorting(searchResult.resultSet());
        } catch (Exception exception) {
            LOGGER.error(exception, exception::getMessage);
            fail("Failed with unexpected exception");
        }
    }

    /**
     * (1) Create incidents for all combinations of "type" and "severity"
     * (2) Search by type = "FIRE" and check number and IDs of result items
     * (3) Search by type = "POLICE" and severity = "LOW" and check number and IDs of result items
     * (4) Find created incident with type = "MEDICAL" and severity = "MEDIUM" and take its timestamp.
     *     Search by these three criteria and check correct result.
     */
    @Test
    void testSearchCombined() {
        try {
            assertIndexIsEmpty();

            // (1) Create incidents for all combinations of "type" and "severity"
            final var createdIncidentsMap = createIncidentsProduct();
            refreshAndFlushIndex();

            // (2) Search by type = "FIRE" and check number and IDs of result items
            {
                final var searchResult = incidentsService.search(
                        new SearchCriteria(0, 10000,
                                IncidentType.FIRE,
                                null, null, null));

                assertEquals(IncidentType.values().length, searchResult.resultSet().size());
                assertCorrectSorting(searchResult.resultSet());
                assertEquals(
                        createdIncidentsMap.entrySet().stream()
                                .filter(entry -> entry.getValue().type() == IncidentType.FIRE)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toSet()),
                        searchResult.resultSet().stream()
                                .map(Incident::id)
                                .collect(Collectors.toSet()));
            }

            // (3) Search by type = "POLICE" and severity = "LOW" and check number and IDs of result items
            {
                final var searchResult = incidentsService.search(
                        new SearchCriteria(0, 10000,
                                IncidentType.FIRE,
                                null, null,
                                IncidentSeverity.LOW));

                assertEquals(1, searchResult.resultSet().size());
                assertCorrectSorting(searchResult.resultSet());
                assertEquals(
                        createdIncidentsMap.entrySet().stream()
                                .filter(entry -> entry.getValue().type() == IncidentType.FIRE &&
                                        entry.getValue().severity() == IncidentSeverity.LOW)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toSet()),
                        searchResult.resultSet().stream()
                                .map(Incident::id)
                                .collect(Collectors.toSet()));
            }

            // (4) Find created incident with type = "MEDICAL" and severity = "MEDIUM" and take its timestamp.
            //     Search by these three criteria and check correct result.
            {
                final var expectedIncidents = createdIncidentsMap.values().stream()
                        .filter(incident -> incident.type() == IncidentType.MEDICAL &&
                                incident.severity() == IncidentSeverity.MEDIUM)
                        .toList();
                assertEquals(1, expectedIncidents.size());

                final var searchResult = incidentsService.search(
                        new SearchCriteria(0, 10000,
                                IncidentType.MEDICAL, null,
                                new SearchCriteria.TimestampRange(
                                        expectedIncidents.get(0).timestamp(),
                                        expectedIncidents.get(0).timestamp()),
                                IncidentSeverity.MEDIUM));

                assertEquals(1, searchResult.resultSet().size());
                assertEquals(expectedIncidents.get(0).id(), searchResult.resultSet().get(0).id());
            }
        } catch (Exception exception) {
            LOGGER.error(exception, exception::getMessage);
            fail("Failed with unexpected exception");
        }
    }

    /**
     * Search by GeoPoints and distance in km.
     * Use three positions in and around Klagenfurt with different incident type.
     * Then start searching from the city center with increasing distance.
     * Repeat the test with additional filtering using the incident type.
     */
    @Test
    void testSearchGeoPoint() {
        try {
            assertIndexIsEmpty();

            final var cityArcadenId =
                    incidentsService.logIncident(new Incident(UUID.randomUUID(), IncidentType.FIRE,
                            new Location(46.62795042216567, 14.30954467999161),
                            Instant.now(), IncidentSeverity.MEDIUM));
            final var cineCityId =
                    incidentsService.logIncident(new Incident(UUID.randomUUID(), IncidentType.POLICE,
                            new Location(46.63014491399003, 14.348243786073173),
                            Instant.now(), IncidentSeverity.MEDIUM));
            final var casinoVeldenId =
                    incidentsService.logIncident(new Incident(UUID.randomUUID(), IncidentType.MEDICAL,
                            new Location(46.615415015670074, 14.043264411560829),
                            Instant.now(), IncidentSeverity.MEDIUM));

            refreshAndFlushIndex();

            final var cityCenter = new Location(46.62410171042155, 14.307600105143639);

            // Search incidents up to 1km away from city center - supposed to find "City Arcaden" only
            {
                final var searchResult = incidentsService.search(new SearchCriteria(0, 10000,
                        null, new SearchCriteria.LocationAndDistance(cityCenter, 1),
                        null, null));

                assertEquals(1, searchResult.resultSet().size());
                assertEquals(Set.of(cityArcadenId),
                        searchResult.resultSet().stream().map(Incident::id).collect(Collectors.toSet()));
            }

            // Search incidents up to 5km away from city center - supposed to find "City Arcaden" and "CineCity"
            {
                final var searchResult = incidentsService.search(new SearchCriteria(0, 10000,
                        null, new SearchCriteria.LocationAndDistance(cityCenter, 5),
                        null, null));

                assertEquals(2, searchResult.resultSet().size());
                assertEquals(Set.of(cityArcadenId, cineCityId),
                        searchResult.resultSet().stream().map(Incident::id).collect(Collectors.toSet()));
            }

            // Search "MEDICAL"-incidents up to 30km away from city center - supposed to find "Casino Velden" only
            {
                final var searchResult = incidentsService.search(new SearchCriteria(0, 10000,
                        IncidentType.MEDICAL,
                        new SearchCriteria.LocationAndDistance(cityCenter, 30),
                        null, null));

                assertEquals(1, searchResult.resultSet().size());
                assertEquals(Set.of(casinoVeldenId),
                        searchResult.resultSet().stream().map(Incident::id).collect(Collectors.toSet()));
            }
        } catch (Exception exception) {
            LOGGER.error(exception, exception::getMessage);
            fail("Failed with unexpected exception");
        }
    }

    /**
     * Check, that the ES-index used for the test is empty.
     */
    private void assertIndexIsEmpty() throws ESException {
        assertEquals(0L, esClientFactory
                .create()
                .countDocuments(configProperties.incidentsIndexName()));
    }

    /**
     * Check, that the resultSet-items are sorted by "timestamp" descending.
     * @param resultSet a list of incidents
     */
    private void assertCorrectSorting(List<Incident> resultSet) {
        for (int i = 1; i < resultSet.size(); ++i) {
            final var prev = resultSet.get(i - 1);
            final var curr = resultSet.get(i);
            assertTrue(prev.timestamp().compareTo(curr.timestamp()) >= 0);
        }
    }

    /**
     * Create incidents for each combination of type and severity. The location is a random value,
     * for timestamp the current UTC-time will be used.
     *
     * @throws TechnicalException when creating the incident fails
     */
    private Map<UUID, Incident> createIncidentsProduct() throws TechnicalException {
        final HashMap<UUID, Incident> result = new HashMap<>();
        final var random = new Random();

        for (IncidentType type : IncidentType.values()) {
            for (IncidentSeverity severity : IncidentSeverity.values()) {
                final var location = new Location(
                        random.nextDouble(360.0) - 180.0,
                        random.nextDouble(180.0) - 90.0);

                final var incident = new Incident(UUID.randomUUID(), type, location, Instant.now(), severity);
                result.put(incidentsService.logIncident(incident), incident);
            }
        }

        return result;
    }

    /**
     * Perform a flush and a refresh on all ES-indices.
     * Both are synchronous operations that will make the client wait.
     * For unit tests this is necessary.
     *
     * @throws ESException when a call to ES fails
     */
    private void refreshAndFlushIndex() throws ESException {
        esClientFactory.create().flushIndex(configProperties.incidentsIndexName());
        esClientFactory.create().refreshIndex(configProperties.incidentsIndexName());
    }
}
