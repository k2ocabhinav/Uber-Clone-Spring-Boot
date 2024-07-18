package com.github.k2ocabhinav.ubercloneapp.strategies;

import com.github.k2ocabhinav.ubercloneapp.dto.RideRequestDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;

import java.util.List;

public interface DriverMatchingStrategy {
    List<Driver> findMatchingDrivers(RideRequestDto rideRequestDto);
}
