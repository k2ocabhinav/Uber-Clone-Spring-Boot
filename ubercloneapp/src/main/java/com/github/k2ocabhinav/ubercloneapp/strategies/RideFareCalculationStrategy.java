package com.github.k2ocabhinav.ubercloneapp.strategies;

import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;

public interface RideFareCalculationStrategy {

    double RIDE_FARE_MULTIPLIER = 10;

    double calculateFare(RideRequest rideRequest);
}
