package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;

public interface RideRequestService {

    RideRequest findRideRequestById(Long rideRequestId);

    void update(RideRequest rideRequest);
}
