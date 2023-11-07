package com.example.incidents;

import org.hamcrest.core.StringRegularExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IncidentApiTest {

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
                	"type" : "MEDICAL",
                	"location" : {
                		"latitude" : 5.6,
                		"longitude" : 7.8
                	},
                	"timestamp" : "2023-11-05T17:00:00.00Z",
                	"severity" : "MEDIUM"
                }
                """;

        mockMvc.perform(
                put("/incidents/log")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", StringRegularExpression.matchesRegex(UUID_REGEX)));
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
                	"type" : "POLICE",
                	"timestamp" : "2023-11-05T18:00:00.00Z",
                	"severity" : "LOW"
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
                	"type" : "WRONG",
                	"location" : {
                		"latitude" : 5.6,
                		"longitude" : 7.8
                	},
                	"timestamp" : "2023-11-05T17:00:00.00Z",
                	"severity" : "HIGH"
                }
                """;

        mockMvc.perform(
                put("/incidents/log")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqBody))
                .andExpect(status().isBadRequest());
    }
}
