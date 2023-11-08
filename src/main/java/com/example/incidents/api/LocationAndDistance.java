package com.example.incidents.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class LocationAndDistance {
    @NotNull(message = "The location is mandatory")
    Location location;

    @NotNull(message = "The distanceInKm is mandatory")
    @PositiveOrZero(message = "The distanceInKm needs to be greater or equal to 0")
    Integer distanceInKm;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(Integer distanceInKm) {
        this.distanceInKm = distanceInKm;
    }
}
