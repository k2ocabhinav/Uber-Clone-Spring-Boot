package com.github.k2ocabhinav.ubercloneapp.strategies.impl;

import com.github.k2ocabhinav.ubercloneapp.configs.FareConfig;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FareCalculationTest {

    @Mock
    private DistanceService distanceService;

    private FareConfig fareConfig;
    private RiderFareDefaultRideFareCalculationStrategy defaultStrategy;
    private RideFareSurgePricingFareCalculationStrategy surgeStrategy;

    @BeforeEach
    void setUp() {
        fareConfig = new FareConfig();
        fareConfig.setPerKmRate(10.0);
        fareConfig.setSurgeMultiplier(2.0);
        
        defaultStrategy = new RiderFareDefaultRideFareCalculationStrategy(distanceService, fareConfig);
        surgeStrategy = new RideFareSurgePricingFareCalculationStrategy(distanceService, fareConfig);
    }

    @Test
    @DisplayName("Default fare calculation returns distance * rate")
    void defaultFareCalculation() {
        RideRequest rideRequest = createMockRideRequest();
        when(distanceService.calculateDistance(rideRequest.getPickupLocation(), rideRequest.getDropOffLocation()))
                .thenReturn(5.0);
        
        double fare = defaultStrategy.calculateFare(rideRequest);
        
        assertThat(fare).isGreaterThan(0);
        assertThat(fare).isEqualTo(5.0 * 10.0);
    }

    @Test
    @DisplayName("Surge pricing applies 2x multiplier")
    void surgePricingCalculation() {
        RideRequest rideRequest = createMockRideRequest();
        when(distanceService.calculateDistance(rideRequest.getPickupLocation(), rideRequest.getDropOffLocation()))
                .thenReturn(5.0);
        
        double defaultFare = defaultStrategy.calculateFare(rideRequest);
        double surgeFare = surgeStrategy.calculateFare(rideRequest);
        
        assertThat(surgeFare).isEqualTo(defaultFare * 2);
    }

    @Test
    @DisplayName("Zero distance returns zero fare")
    void zeroDistanceFare() {
        RideRequest rideRequest = createMockRideRequest();
        when(distanceService.calculateDistance(rideRequest.getPickupLocation(), rideRequest.getDropOffLocation()))
                .thenReturn(0.0);
        
        double fare = defaultStrategy.calculateFare(rideRequest);
        
        assertThat(fare).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Surge fare is double default fare for same distance")
    void surgeIsDoubleDefault() {
        RideRequest rideRequest = createMockRideRequest();
        when(distanceService.calculateDistance(rideRequest.getPickupLocation(), rideRequest.getDropOffLocation()))
                .thenReturn(10.0);
        
        double defaultFare = defaultStrategy.calculateFare(rideRequest);
        double surgeFare = surgeStrategy.calculateFare(rideRequest);
        
        assertThat(surgeFare).isEqualTo(200.0);
        assertThat(surgeFare).isEqualTo(defaultFare * 2);
    }

    private RideRequest createMockRideRequest() {
        return RideRequest.builder()
                .id(1L)
                .pickupLocation(null)
                .dropOffLocation(null)
                .build();
    }
}
