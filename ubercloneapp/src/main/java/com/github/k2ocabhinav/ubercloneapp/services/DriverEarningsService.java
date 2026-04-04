package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverEarningDto;
import com.github.k2ocabhinav.ubercloneapp.dto.EarningsSummaryDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

public interface DriverEarningsService {
    void createEarningRecord(Ride ride);
    Page<DriverEarningDto> getEarningsHistory(PageRequest pageRequest);
    EarningsSummaryDto getDailySummary(LocalDate date);
    EarningsSummaryDto getWeeklySummary(LocalDate date);
    EarningsSummaryDto getMonthlySummary(LocalDate date);
    Double getAvailableBalance();
}
