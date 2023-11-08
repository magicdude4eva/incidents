package com.example.incidents.service;

import com.example.incidents.common.IncidentSeverity;
import com.example.incidents.common.IncidentType;

import java.time.Instant;
import java.util.UUID;

/**
 * Data class for an incident. Used for saving / retrieving into / from ES.
 *
 * @param id identifier (required)
 * @param type incident type (required)
 * @param location location (required)
 * @param timestamp timestamp of creation (required)
 * @param severity severity level (required)
 */
public record Incident(UUID id,
                       IncidentType type,
                       Location location,
                       Instant timestamp,
                       IncidentSeverity severity) {
}
