package com.example.incidents.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

/**
 * Request body for endpoint "search" in incidents API.
 */
public class SearchRequest {

    // First result item to be included in the result
    @PositiveOrZero(message = "The offset needs to be greater or equal to 0")
    long offset;

    // Number of items in result - if 0, entire result set will be returned
    // The maximum number of items is limited to 10.000,
    // as this is the maximum number of results ES will return in one response.
    @DecimalMin(value = "0", message = "The resultCount needs to be greater or equal to 0")
    @DecimalMax(value = "10000", message = "The resultCount needs to be less or equal to 10.000")
    long resultCount;

    String type;

    ApiLocation location;

    Instant minTimestamp;

    Instant maxTimestamp;

    String severity;

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getResultCount() {
        return resultCount;
    }

    public void setResultCount(long resultCount) {
        this.resultCount = resultCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ApiLocation getLocation() {
        return location;
    }

    public void setLocation(ApiLocation apiLocation) {
        this.location = apiLocation;
    }

    public Instant getMinTimestamp() {
        return minTimestamp;
    }

    public void setMinTimestampe(Instant minTimestamp) {
        this.minTimestamp = minTimestamp;
    }

    public Instant getMaxTimestamp() {
        return maxTimestamp;
    }

    public void setMaxTimestamp(Instant maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
