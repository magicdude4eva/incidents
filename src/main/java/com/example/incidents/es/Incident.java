package com.example.incidents.es;

import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.Instant;
import java.util.UUID;

/**
 * Incident as written / read to and from ES.
 *
 * @param id
 * @param incidentType
 * @param location
 * @param timestamp
 * @param severityLevel
 */
public record Incident(UUID id, String incidentType, GeoPoint location, Instant timestamp, String severityLevel) {
}
