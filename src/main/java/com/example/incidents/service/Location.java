package com.example.incidents.service;

/**
 * Data class for a location.
 *
 * @param lat latitude (required)
 * @param lon longitude (required)
 */
public record Location(double lat, double lon) {
}
