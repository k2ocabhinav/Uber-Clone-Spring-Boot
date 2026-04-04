package com.github.k2ocabhinav.ubercloneapp.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private Long totalUsers;
    private Long totalDrivers;
    private Long totalRides;
    private Double totalRevenue;
    private Long activeRides;
    private Long pendingApprovals;
    private Map<String, Long> ridesByStatus;
    private Map<String, Double> revenueByDay;
}
