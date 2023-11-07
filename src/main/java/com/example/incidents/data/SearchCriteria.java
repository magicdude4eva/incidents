package com.example.incidents.data;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @param offset
 * @param resultCount
 * @param type
 * @param locationAndDistance
 * @param timestampRange
 * @param severity
 */
public record SearchCriteria(int offset, int resultCount,
                             IncidentType type, LocationAndDistance locationAndDistance,
                             TimestampRange timestampRange,
                             IncidentSeverity severity) {

    public record LocationAndDistance(Location location, int distanceInKm) {

    }

    public record TimestampRange(Instant minTimestamp, Instant maxTimestamp) {
    }

    public boolean hasSearchCriteria() {
        return Stream.of(type, locationAndDistance, timestampRange, severity).anyMatch(Objects::nonNull);
    }
}
