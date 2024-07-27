package com.github.k2ocabhinav.ubercloneapp.strategies.impl;

import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.strategies.DriverMatchingStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.FareCalculationStrategy;
import org.springframework.stereotype.Service;

import java.util.List;


public class RiderFareSurgePricingFareCalculationStrategy implements FareCalculationStrategy {

    @Override
    public double calculateFare(RideRequest rideRequest) {
        return 0;
    }
}
