package com.github.k2ocabhinav.ubercloneapp.strategies.impl;


import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiderFareDefaultRideFareCalculationStrategy implements RideFareCalculationStrategy {

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
