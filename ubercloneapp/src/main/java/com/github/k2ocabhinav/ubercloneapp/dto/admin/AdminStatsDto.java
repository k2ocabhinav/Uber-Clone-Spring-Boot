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
public class AdminStatsDto {
    private LocalDate date;
    private Long totalRides;
    private Long completedRides;
    private Long cancelledRides;
    private BigDecimal totalRevenue;
    private BigDecimal platformCommission;
    private Long totalUsers;
    private Long newUsers;
    private Long activeDrivers;
    private Long pendingDriverApprovals;
}
