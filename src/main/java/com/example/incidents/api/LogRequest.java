package com.example.incidents.api;

import com.example.incidents.common.IncidentSeverity;
import com.example.incidents.common.IncidentType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Request body for endpoint "log" in incidents API.
 */
public class LogRequest {

    @NotNull(message = "The type is required")
    IncidentType type;

    @NotNull(message = "The Location is required")
    Location location;

    @NotNull(message = "The timestamp is required")
    Instant timestamp;

    @NotNull(message = "The severity is required")
    IncidentSeverity severity;

    public IncidentType getType() {
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }
}
