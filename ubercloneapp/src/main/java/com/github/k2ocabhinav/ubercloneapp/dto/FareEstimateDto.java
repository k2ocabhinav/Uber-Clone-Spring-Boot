package com.github.k2ocabhinav.ubercloneapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareEstimateDto {
    private BigDecimal estimatedFare;
    private Double distanceKm;
    private Double durationMinutes;
    private Double surgeMultiplier;
    private Boolean surgeActive;
    private java.time.LocalDateTime estimatedPickupTime;
    private Double promoDiscountStub;
}
