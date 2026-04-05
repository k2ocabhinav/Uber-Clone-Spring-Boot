package com.github.k2ocabhinav.ubercloneapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverEarningDto {
    private Long id;
    private Long rideId;
    private BigDecimal grossFare;
    private BigDecimal platformCommission;
    private BigDecimal netEarning;
    private LocalDateTime createdTime;
}
