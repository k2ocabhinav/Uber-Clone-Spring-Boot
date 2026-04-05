package com.github.k2ocabhinav.ubercloneapp.strategies;

import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import java.math.BigDecimal;

public interface RideFareCalculationStrategy {
    BigDecimal calculateFare(RideRequest rideRequest);
}
