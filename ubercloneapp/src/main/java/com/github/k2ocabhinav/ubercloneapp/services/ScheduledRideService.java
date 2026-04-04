package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.RideRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.ScheduledRideDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

public interface ScheduledRideService {

    ScheduledRideDto scheduleRide(RideRequestDto rideRequestDto);

    ScheduledRideDto cancelScheduledRide(Long rideRequestId);

    ScheduledRideDto rescheduleRide(Long rideRequestId, LocalDateTime newScheduledTime);

    Page<ScheduledRideDto> getMyScheduledRides(PageRequest pageRequest);

    Page<ScheduledRideDto> getAllScheduledRides(PageRequest pageRequest);

    int dispatchScheduledRides();
}
