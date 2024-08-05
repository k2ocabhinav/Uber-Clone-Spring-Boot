package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RideDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RiderDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;

import java.util.List;

public interface DriverService {

    RideDto acceptRide(Long rideId);

    RiderDto cancelRide(Long rideId);

    RideDto startRide(Long rideId, String otp);

    RiderDto endRide(Long rideId);

    RiderDto rateRider(Long rideId, Integer rating);

    DriverDto getMyProfile();

    List<RideDto> getAllMyRides();

    Driver getCurrentDriver();
}
