package com.github.k2ocabhinav.ubercloneapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeResultDto {
    private boolean valid;
    private BigDecimal discountAmount;
    private BigDecimal originalFare;
    private BigDecimal discountedFare;
    private String message;
}
