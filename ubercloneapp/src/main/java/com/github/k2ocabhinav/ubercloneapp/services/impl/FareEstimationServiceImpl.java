package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateDto;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateRequestDto;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import com.github.k2ocabhinav.ubercloneapp.services.FareEstimationService;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FareEstimationServiceImpl implements FareEstimationService {

    private final DistanceService distanceService;
    private final RideStrategyManager rideStrategyManager;
    private final ModelMapper modelMapper;

    @Override
    public FareEstimateDto estimateFare(FareEstimateRequestDto fareEstimateRequestDto) {
        RideRequest dummyRideRequest = modelMapper.map(fareEstimateRequestDto, RideRequest.class);
        
        Point pickupPoint = dummyRideRequest.getPickupLocation();
        Point dropOffPoint = dummyRideRequest.getDropOffLocation();
        
        double[] distanceDuration = distanceService.calculateDistanceAndDuration(pickupPoint, dropOffPoint);
        double distanceKm = distanceDuration[0];
        double durationMins = distanceDuration[1];
        
        double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(dummyRideRequest);
        
        return FareEstimateDto.builder()
                .estimatedFare(BigDecimal.valueOf(fare))
                .distanceKm(distanceKm)
                .durationMinutes(durationMins)
                .build();
    }
}
