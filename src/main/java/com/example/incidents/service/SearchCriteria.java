package com.example.incidents.service;

import com.example.incidents.common.IncidentSeverityLevel;
import com.example.incidents.common.IncidentType;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Search criteria for IncidentsService routines.
 *
 * @param offset
 * @param resultCount
 * @param incidentType
 * @param locationAndDistance
 * @param timestampRange
 * @param severityLevel
 */
public record SearchCriteria(int offset, int resultCount,
                             IncidentType incidentType, LocationAndDistance locationAndDistance,
                             TimestampRange timestampRange,
                             IncidentSeverityLevel severityLevel) {

    public record LocationAndDistance(Location location, int distanceInKm) {

    }

    public record TimestampRange(Instant minTimestamp, Instant maxTimestamp) {
    }

    /**
     * Check, whether at least on search criteria is given.
     *
     * @return true, if there is at least on search criteria, and false else
     */
    public boolean hasSearchCriteria() {
        return Stream.of(incidentType, locationAndDistance, timestampRange, severityLevel).anyMatch(Objects::nonNull);
    }
}
