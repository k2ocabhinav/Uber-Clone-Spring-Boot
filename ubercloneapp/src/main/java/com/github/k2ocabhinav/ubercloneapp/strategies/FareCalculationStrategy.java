package com.github.k2ocabhinav.ubercloneapp.strategies;

import com.github.k2ocabhinav.ubercloneapp.dto.RideRequestDto;

public interface FareCalculationStrategy {

    double calculateFare(RideRequestDto rideRequestDto);
}
