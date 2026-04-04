package com.github.k2ocabhinav.ubercloneapp.strategies;

import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;

public interface RideFareCalculationStrategy {
    double calculateFare(RideRequest rideRequest);
}
