package com.github.k2ocabhinav.ubercloneapp.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDto {
    private LocalDate date;
    private BigDecimal grossRevenue;
    private BigDecimal platformCommission;
    private BigDecimal driverEarnings;
    private Long rideCount;
}
