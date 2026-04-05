package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.configs.FareConfig;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateDto;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PromoCodeResultDto;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import com.github.k2ocabhinav.ubercloneapp.services.FareEstimationService;
import com.github.k2ocabhinav.ubercloneapp.services.PromoCodeService;
import com.github.k2ocabhinav.ubercloneapp.services.RiderService;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FareEstimationServiceImpl implements FareEstimationService {

    private final DistanceService distanceService;
    private final RideStrategyManager rideStrategyManager;
    private final PromoCodeService promoCodeService;
    private final RiderService riderService;
    private final FareConfig fareConfig;
    private final ModelMapper modelMapper;

    @Override
    public FareEstimateDto estimateFare(FareEstimateRequestDto fareEstimateRequestDto) {
        RideRequest dummyRideRequest = modelMapper.map(fareEstimateRequestDto, RideRequest.class);
        
        Point pickupPoint = dummyRideRequest.getPickupLocation();
        Point dropOffPoint = dummyRideRequest.getDropOffLocation();
        
        double[] distanceDuration = distanceService.calculateDistanceAndDuration(pickupPoint, dropOffPoint);
        double distanceKm = distanceDuration[0];
        double durationMins = distanceDuration[1];
        
        BigDecimal baseFare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(dummyRideRequest);
        BigDecimal finalFare = baseFare;
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        String promoCode = fareEstimateRequestDto.getPromoCode();
        if (promoCode != null && !promoCode.isBlank()) {
            try {
                Rider rider = riderService.getCurrentRider();
                PromoCodeResultDto promoResult = promoCodeService.validateAndApplyPromo(promoCode, rider.getUser(), baseFare);
                if (promoResult.isValid()) {
                    discountAmount = promoResult.getDiscountAmount();
                    finalFare = promoResult.getDiscountedFare();
                }
            } catch (Exception e) {
                log.warn("Could not apply promo code for estimate: {}", e.getMessage());
            }
        }
        
        return FareEstimateDto.builder()
                .estimatedFare(finalFare)
                .baseFare(baseFare)
                .surgeMultiplier(fareConfig.getSurgeMultiplier()) // Or dynamic if implemented
                .discountAmount(discountAmount)
                .distanceKm(distanceKm)
                .durationMinutes(durationMins)
                .estimatedPickupTime(LocalDateTime.now().plusMinutes(10)) // Static stub for v1.1.0
                .applicablePromos(promoCodeService.getActivePromoCodes())
                .build();
    }
}
