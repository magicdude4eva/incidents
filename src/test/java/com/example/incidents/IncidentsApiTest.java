package com.example.incidents;

import com.example.incidents.es.Incident;
import com.example.incidents.mapper.CommonObjectMapper;
import com.example.incidents.service.SearchResult;
import org.hamcrest.core.StringRegularExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the API (endpoints "/incidents/log" and "/incidents/search".
 */
@SpringBootTest
@AutoConfigureMockMvc
public class IncidentsApiTest extends BaseTest {

    // Regular expression to identify a UUID.
    private final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$");

    @Autowired
    MockMvc mockMvc;

    /**
     * Send a valid request to "/incidents/log" and expect a response of the following form:
     * {
     *    "id" = "UUID"
     * }
     *
     * @throws Exception on a technical error
     */
    @Test
    void testLogIncidentSuccess() throws Exception {
        final var reqBody = """
                {
                	"incidentType" : "MEDICAL",
                	"location" : {
                		"lat" : 5.6,
                		"lon" : 7.8
                	},
                	"timestamp" : "2023-11-05T17:00:00.00Z",
                	"severityLevel" : "MEDIUM"
                }
                """;

        mockMvc.perform(
                put("/incidents/log")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", StringRegularExpression.matchesRegex(UUID_REGEX)));
    }

    /**
     * Send an incomplete request to "/incidents/log" and expect a BAD_REQUEST response
     *
     * @throws Exception on a technical error
     */
    @Test
    void testLogIncidentIncomplete() throws Exception {
        // This request lacks the "location" values
        final var reqBody = """
                {
                	"incidentType" : "POLICE",
                	"timestamp" : "2023-11-05T18:00:00.00Z",
                	"severityLevel" : "LOW"
                }
                """;

        mockMvc.perform(
                put("/incidents/log")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Send a request with an invalid enumeration value to "/incidents/log" and expect a BAD_REQUEST response
     *
     * @throws Exception on a technical error
     */
    @Test
    void testLogIncidentsInvalid() throws Exception {
        final var reqBody = """
                {
                	"incidentType" : "WRONG",
                	"location" : {
                		"lat" : 5.6,
                		"lon" : 7.8
                	},
                	"timestamp" : "2023-11-05T17:00:00.00Z",
                	"severityLevel" : "HIGH"
                }
                """;

        mockMvc.perform(
                put("/incidents/log")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Search indices without criteria, but limit the result count to 5.
     * Expect the most recent 5 entries to be found.
     */
    @Test
    void testSearchLastFive() throws Exception {
        final var reqBody = """
                {
                    "offset" : 0,
                    "resultCount" : 5
                }
                """;

        final var mapCreatedProducts = createIncidentsProduct();
        flushAndRefreshIndex();

        final var lastFiveUUIDs = mapCreatedProducts.values().stream()
                .sorted(Comparator.comparing(Incident::timestamp).reversed())
                .map(Incident::id)
                .limit(5)
                .toList();

        final var resultActions = mockMvc.perform(
                post("/incidents/search")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqBody))
                .andExpect(status().isOk());

        final var response = resultActions.andReturn();
        final var resultSet = CommonObjectMapper.create().readValue(
                response.getResponse().getContentAsString(),
                SearchResult.class);

        assertEquals(resultSet.resultCount(), 5);
        assertEquals(resultSet.totalCount(), mapCreatedProducts.size());
        for (int i = 0; i < 5; ++i) {
            assertEquals(lastFiveUUIDs.get(i), resultSet.resultSet().get(i).id());
        }
    }

    /**
     * Send an empty request to "/incidents/search" and expect a BAD_REQUEST response
     *
     * @throws Exception on a technical error
     */
    @Test
    void testSearchEmpty() throws Exception {
        final var reqBody = """
                {
                }
                """;

        mockMvc.perform(
                        post("/incidents/search")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(reqBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Send a request with an incomplete "locationAndDistance"-element to "/incidents/search"
     * and expect a BAD_REQUEST response
     *
     * @throws Exception on a technical error
     */
    @Test
    void testSearchIncompleteLocationAndDistance() throws Exception {
        final var reqBody = """
                {
                    "offset" : 0,
                    "resultCount" : 10000,
                    "locationAndDistance" : {
                        "distanceInKm" : 10
                    }
                }
                """;

        mockMvc.perform(
                        post("/incidents/search")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(reqBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Send a request with an invalid "timestampRange" (min > max) to "/incidents/search"
     * and expect a UNPROCESSABLE_ENTITY response
     *
     * @throws Exception on a technical error
     */
    @Test
    void testSearchInvalidTimestampRange() throws Exception {
        final var reqBody = """
                {
                    "offset" : 0,
                    "resultCount" : 10000,
                    "timestampRange" : {
                        "minTimestamp" : "2023-11-02T00:00:00Z",
                        "maxTimestamp" : "2023-11-01T00:00:00Z"
                    }
                }
                """;

        mockMvc.perform(
                        post("/incidents/search")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(reqBody))
                .andExpect(status().isUnprocessableEntity());
    }
}
