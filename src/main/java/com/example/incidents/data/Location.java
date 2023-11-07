package com.example.incidents.data;

/**
 * Data class for a location.
 *
 * @param lat latitude (required)
 * @param lon longitude (required)
 */
public record Location(double lon, double lat) {
}
