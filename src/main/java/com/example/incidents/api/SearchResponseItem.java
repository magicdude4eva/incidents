package com.example.incidents.api;

import com.example.incidents.common.IncidentSeverity;
import com.example.incidents.common.IncidentType;

import java.time.Instant;

public class SearchResponseItem {

    String id;
    IncidentType type;
    Location location;
    Instant timestamp;
    IncidentSeverity severity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
