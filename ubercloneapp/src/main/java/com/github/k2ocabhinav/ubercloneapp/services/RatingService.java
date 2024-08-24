package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RiderDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;

public interface RatingService {
    DriverDto rateDriver(Ride ride, Integer rating);
    RiderDto rateRider(Ride ride, Integer rating);

    void createNewRating(Ride ride);
}
