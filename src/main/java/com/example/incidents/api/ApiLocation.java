package com.example.incidents.api;

import jakarta.validation.constraints.NotNull;

/**
 * Holds a location that will be used for API requests and responses.
 * The fields "latitude" and "longitude" are mandatory.
 */
public class ApiLocation {
    @NotNull
    double lat;
    @NotNull
    double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
