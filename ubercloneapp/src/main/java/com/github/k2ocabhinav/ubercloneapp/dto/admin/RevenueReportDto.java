package com.github.k2ocabhinav.ubercloneapp.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDto {
    private LocalDate date;
    private Double grossRevenue;
    private Double platformCommission;
    private Double driverEarnings;
    private Long rideCount;
}
