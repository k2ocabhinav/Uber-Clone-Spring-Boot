package com.github.k2ocabhinav.ubercloneapp.dto;

import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

public class RideDto {

    private Long id;
    private Point pickupLocation;
    private Point dropOffLocation;
    private LocalDateTime createdTime;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private RiderDto rider;
    private DriverDto driver;

    private String otp;
    private PaymentMethod paymentMethod;
    private RideStatus rideStatus;
    private Double fair;
}
