package com.example.incidents.service;

import com.example.incidents.common.IncidentSeverityLevel;
import com.example.incidents.common.IncidentType;

import java.time.Instant;
import java.util.UUID;

/**
 * Data class for an incident. Used for all service routines.
 *
 * @param id identifier (required)
 * @param incidentType incident incidentType (required)
 * @param location location (required)
 * @param timestamp timestamp of creation (required)
 * @param severityLevel severityLevel level (required)
 */
public record Incident(UUID id,
                       IncidentType incidentType,
                       Location location,
                       Instant timestamp,
                       IncidentSeverityLevel severityLevel) {
}
