package com.github.k2ocabhinav.ubercloneapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarningsSummaryDto {
    private BigDecimal totalGross;
    private BigDecimal totalCommission;
    private BigDecimal totalNet;
    private Long rideCount;
    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public EarningsSummaryDto(BigDecimal totalGross, BigDecimal totalCommission, BigDecimal totalNet, Long rideCount, LocalDateTime startDate, LocalDateTime endDate) {
        this.totalGross = totalGross == null ? BigDecimal.ZERO : totalGross;
        this.totalCommission = totalCommission == null ? BigDecimal.ZERO : totalCommission;
        this.totalNet = totalNet == null ? BigDecimal.ZERO : totalNet;
        this.rideCount = rideCount == null ? 0L : rideCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
