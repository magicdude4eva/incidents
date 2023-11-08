package com.example.incidents.api;

import com.example.incidents.common.IncidentSeverityLevel;
import com.example.incidents.common.IncidentType;

import java.time.Instant;

/**
 * One item in the search response.
 */
public class SearchResponseItem {

    String id;
    IncidentType incidentType;
    Location location;
    Instant timestamp;
    IncidentSeverityLevel severityLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
