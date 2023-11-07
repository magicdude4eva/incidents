package com.example.incidents.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Request body for endpoint "log" in incidents API.
 */
public class LogRequest {

    @NotBlank(message = "The type is required")
    String type;

    @NotNull(message = "The Location is required")
    ApiLocation apiLocation;

    @NotNull(message = "The timestamp is required")
    Instant timestamp;

    @NotBlank(message = "The severity is required")
    String severity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ApiLocation getLocation() {
        return apiLocation;
    }

    public void setLocation(ApiLocation apiLocation) {
        this.apiLocation = apiLocation;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
