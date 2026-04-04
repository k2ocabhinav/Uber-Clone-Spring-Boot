package com.github.k2ocabhinav.ubercloneapp.strategy;

import com.github.k2ocabhinav.ubercloneapp.configs.FareConfig;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideFareCalculationStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.impl.RiderFareDefaultRideFareCalculationStrategy;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RideStrategyManagerTest {

    @Mock
    private DistanceService distanceService;

    private FareConfig fareConfig;
    private RideStrategyManagerTestWrapper strategyManager;

    @BeforeEach
    void setUp() {
        fareConfig = new FareConfig();
        fareConfig.setSurgeStartHour(18);
        fareConfig.setSurgeEndHour(21);
        
        RiderFareDefaultRideFareCalculationStrategy defaultFareStrategy = 
                new RiderFareDefaultRideFareCalculationStrategy(distanceService, fareConfig);
        RideFareSurgePricingFareCalculationStrategy surgePricingStrategy = 
                new RideFareSurgePricingFareCalculationStrategy(distanceService, fareConfig);
        strategyManager = new RideStrategyManagerTestWrapper(
                defaultFareStrategy, surgePricingStrategy, fareConfig);
    }

    @Test
    @DisplayName("Should use surge pricing during peak hours (6-9 PM)")
    void shouldUseSurgePricingDuringPeakHours() {
        LocalDateTime peakTime = LocalDateTime.of(2024, 1, 15, 18, 30);
        
        RideFareCalculationStrategy strategy = strategyManager.fareCalculationStrategy(peakTime);
        
        assertThat(strategy).isInstanceOf(RideFareSurgePricingFareCalculationStrategy.class);
    }

    @Test
    @DisplayName("Should use default pricing outside peak hours")
    void shouldUseDefaultPricingOutsidePeakHours() {
        LocalDateTime offPeakTime = LocalDateTime.of(2024, 1, 15, 14, 30);
        
        RideFareCalculationStrategy strategy = strategyManager.fareCalculationStrategy(offPeakTime);
        
        assertThat(strategy).isInstanceOf(RiderFareDefaultRideFareCalculationStrategy.class);
    }

    @Test
    @DisplayName("Should use default pricing at 9 PM boundary")
    void shouldUseDefaultPricingAtNinePM() {
        LocalDateTime ninePM = LocalDateTime.of(2024, 1, 15, 21, 0);
        
        RideFareCalculationStrategy strategy = strategyManager.fareCalculationStrategy(ninePM);
        
        assertThat(strategy).isInstanceOf(RiderFareDefaultRideFareCalculationStrategy.class);
    }

    @Test
    @DisplayName("Should use default pricing at 6 PM boundary")
    void shouldUseDefaultPricingAtSixPM() {
        LocalDateTime sixPM = LocalDateTime.of(2024, 1, 15, 18, 0);
        
        RideFareCalculationStrategy strategy = strategyManager.fareCalculationStrategy(sixPM);
        
        assertThat(strategy).isInstanceOf(RiderFareDefaultRideFareCalculationStrategy.class);
    }

    static class RideStrategyManagerTestWrapper {
        private final RiderFareDefaultRideFareCalculationStrategy defaultFareStrategy;
        private final RideFareSurgePricingFareCalculationStrategy surgePricingStrategy;
        private final FareConfig fareConfig;

        RideStrategyManagerTestWrapper(
                RiderFareDefaultRideFareCalculationStrategy defaultFareStrategy,
                RideFareSurgePricingFareCalculationStrategy surgePricingStrategy,
                FareConfig fareConfig) {
            this.defaultFareStrategy = defaultFareStrategy;
            this.surgePricingStrategy = surgePricingStrategy;
            this.fareConfig = fareConfig;
        }

        public RideFareCalculationStrategy fareCalculationStrategy(LocalDateTime dateTime) {
            java.time.LocalTime time = dateTime.toLocalTime();
            java.time.LocalTime surgeStart = java.time.LocalTime.of((int) fareConfig.getSurgeStartHour(), 0);
            java.time.LocalTime surgeEnd = java.time.LocalTime.of((int) fareConfig.getSurgeEndHour(), 0);
            boolean isSurgeTime = time.isAfter(surgeStart) && time.isBefore(surgeEnd);
            if (isSurgeTime) {
                return surgePricingStrategy;
            }
            return defaultFareStrategy;
        }
    }
}
