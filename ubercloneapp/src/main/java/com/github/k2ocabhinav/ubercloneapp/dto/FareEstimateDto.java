package com.github.k2ocabhinav.ubercloneapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareEstimateDto {
    private BigDecimal estimatedFare; // final total after surge and discount
    private BigDecimal baseFare; // fare before surge and discount
    private Double surgeMultiplier;
    private BigDecimal discountAmount; // discount applied
    private Double distanceKm;
    private Double durationMinutes; // keeping as Double if service returns double
    private LocalDateTime estimatedPickupTime;
    private List<PromoCodeDto> applicablePromos;
}
