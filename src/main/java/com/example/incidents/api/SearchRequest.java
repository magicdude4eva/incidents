package com.example.incidents.api;

import com.example.incidents.common.IncidentSeverity;
import com.example.incidents.common.IncidentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

/**
 * Request body for endpoint "search" in incidents API.
 */
public class SearchRequest {

    // First result item to be included in the result
    @NotNull(message = "The offset is mandatory")
    @PositiveOrZero(message = "The offset needs to be greater or equal to 0")
    Integer offset;

    // Number of items in result - if 0, entire result set will be returned
    // The maximum number of items is limited to 10.000,
    // as this is the maximum number of results ES will return in one response.
    @NotNull(message = "The resultCount is mandatory")
    @DecimalMin(value = "0", message = "The resultCount needs to be greater or equal to 0")
    @DecimalMax(value = "10000", message = "The resultCount needs to be less or equal to 10.000")
    Integer resultCount;

    @Valid
    IncidentType type;

    // Location and distance for searching - allowed to be null.
    // If this element exists, it needs to be complete.
    @Valid
    LocationAndDistance locationAndDistance;

    // Timestamp range for searching - allowed to be null.
    // If this element exists, it needs to be complete.
    @Valid
    TimestampRange timestampRange;

    @Valid
    IncidentSeverity severity;

    public static class TimestampRange {
        @NotNull(message = "The minTimestamp is mandatory")
        Instant minTimestamp;

        @NotNull(message = "The maxTimestamp is mandatory")
        Instant maxTimestamp;

        public Instant getMinTimestamp() {
            return minTimestamp;
        }

        public void setMinTimestamp(Instant minTimestamp) {
            this.minTimestamp = minTimestamp;
        }

        public Instant getMaxTimestamp() {
            return maxTimestamp;
        }

        public void setMaxTimestamp(Instant maxTimestamp) {
            this.maxTimestamp = maxTimestamp;
        }
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public IncidentType getType() {
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }

    public LocationAndDistance getLocationAndDistance() {
        return locationAndDistance;
    }

    public void setLocationAndDistance(LocationAndDistance locationAndDistance) {
        this.locationAndDistance = locationAndDistance;
    }

    public TimestampRange getTimestampRange() {
        return timestampRange;
    }

    public void setTimestampRange(TimestampRange timestampRange) {
        this.timestampRange = timestampRange;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }
}
