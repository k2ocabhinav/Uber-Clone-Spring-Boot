package com.github.k2ocabhinav.ubercloneapp.strategies.impl;


import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import com.github.k2ocabhinav.ubercloneapp.strategies.FareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiderFareDefaultFareCalculationStrategy implements FareCalculationStrategy {

    private final DistanceService distanceService;

    @Override
    public double calculateFare(RideRequest rideRequest) {
        double distance = distanceService
                .calculateDistance(
                        rideRequest.getPickupLocation(),
                        rideRequest.getDropOffLocation());
        return distance * RIDE_FARE_MULTIPLIER;
    }
}
