package com.example.incidents.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Holds a location that will be used for API requests and responses.
 * The fields "latitude" and "longitude" are mandatory.
 */
public class Location {
    @Valid
    @NotNull(message = "The lat(itude) is mandatory")
    Double lat;

    @Valid
    @NotNull(message = "The lon(gitude) is mandatory")
    Double lon;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
