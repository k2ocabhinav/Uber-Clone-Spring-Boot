package com.github.k2ocabhinav.ubercloneapp.strategies.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.RideRequestDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.strategies.DriverMatchingStrategy;

import java.util.List;

public class DriverMatchingSurgePricingFareCalculationStrategy implements DriverMatchingStrategy {
    @Override
    public List<Driver> findMatchingDrivers(RideRequestDto rideRequestDto) {
        return List.of();
    }
}
