package com.github.k2ocabhinav.ubercloneapp.strategies;

import com.github.k2ocabhinav.ubercloneapp.configs.FareConfig;
import com.github.k2ocabhinav.ubercloneapp.configs.PlatformConfig;
import com.github.k2ocabhinav.ubercloneapp.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.impl.DriverMatchingNearestDriverStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.impl.RiderFareDefaultRideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class RideStrategyManager {

    private final DriverMatchingHighestRatedDriverStrategy highestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategy nearestDriverStrategy;
    private final RideFareSurgePricingFareCalculationStrategy surgePricingFareCalculationStrategy;
    private final RiderFareDefaultRideFareCalculationStrategy defaultFareCalculationStrategy;
    private final PlatformConfig platformConfig;
    private final FareConfig fareConfig;

    public DriverMatchingStrategy driverMatchingStrategy(double riderRating) {
        if(riderRating >= platformConfig.getDriverRatingThreshold()) {
            return highestRatedDriverStrategy;
        } else {
            return nearestDriverStrategy;
        }
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy() {
        LocalTime surgeStartTime = LocalTime.of((int) fareConfig.getSurgeStartHour(), 0);
        LocalTime surgeEndTime = LocalTime.of((int) fareConfig.getSurgeEndHour(), 0);
        LocalTime currentTime = LocalTime.now();

        boolean isSurgeTime = currentTime.isAfter(surgeStartTime) && currentTime.isBefore(surgeEndTime);

        if(isSurgeTime) {
            return surgePricingFareCalculationStrategy;
        } else {
            return defaultFareCalculationStrategy;
        }
    }

}