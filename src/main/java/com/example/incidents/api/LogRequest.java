package com.example.incidents.api;

import com.example.incidents.common.IncidentSeverityLevel;
import com.example.incidents.common.IncidentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Request body for endpoint "/incidents/log" in incidents API.
 */
public class LogRequest {

    @Valid
    @NotNull(message = "The incidentType is required")
    IncidentType incidentType;

    @Valid
    @NotNull(message = "The Location is required")
    Location location;

    @NotNull(message = "The timestamp is required")
    Instant timestamp;

    @Valid
    @NotNull(message = "The severityLevel is required")
    IncidentSeverityLevel severityLevel;

    public IncidentType getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(IncidentType incidentType) {
        this.incidentType = incidentType;
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

    public IncidentSeverityLevel getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(IncidentSeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
    }
}
