package com.github.k2ocabhinav.ubercloneapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarningsSummaryDto {
    private Double totalGross;
    private Double totalCommission;
    private Double totalNet;
    private Long rideCount;
    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public EarningsSummaryDto(Double totalGross, Double totalCommission, Double totalNet, Long rideCount, LocalDateTime startDate, LocalDateTime endDate) {
        this.totalGross = totalGross == null ? 0.0 : totalGross;
        this.totalCommission = totalCommission == null ? 0.0 : totalCommission;
        this.totalNet = totalNet == null ? 0.0 : totalNet;
        this.rideCount = rideCount == null ? 0L : rideCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
