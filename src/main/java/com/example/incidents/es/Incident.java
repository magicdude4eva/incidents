package com.example.incidents.es;

import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.Instant;
import java.util.UUID;

/**
 * Incident as written / read to and from ES.
 *
 * @param id
 * @param type
 * @param location
 * @param timestamp
 * @param severity
 */
public record Incident(UUID id, String type, GeoPoint location, Instant timestamp, String severity) {
}
