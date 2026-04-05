package com.github.k2ocabhinav.ubercloneapp.dto;

import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FareEstimateRequestDto {
    private PointDto pickupLocation;
    private PointDto dropOffLocation;
    private PaymentMethod paymentMethod;
}
