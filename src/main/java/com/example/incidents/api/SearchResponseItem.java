package com.example.incidents.api;

import java.time.Instant;

public class SearchResponseItem {

    String id;
    String type;
    ApiLocation apiLocation;
    Instant timestamp;
    String severity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
