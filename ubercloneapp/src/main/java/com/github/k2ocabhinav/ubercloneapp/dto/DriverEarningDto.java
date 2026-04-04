package com.github.k2ocabhinav.ubercloneapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverEarningDto {
    private Long id;
    private Long rideId;
    private Double grossFare;
    private Double platformCommission;
    private Double netEarning;
    private LocalDateTime createdTime;
}
