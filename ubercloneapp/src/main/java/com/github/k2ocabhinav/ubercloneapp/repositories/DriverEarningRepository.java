package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.dto.EarningsSummaryDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.DriverEarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DriverEarningRepository extends JpaRepository<DriverEarning, Long> {
    Page<DriverEarning> findByDriverOrderByCreatedTimeDesc(Driver driver, Pageable pageable);

    @Query("SELECT new com.github.k2ocabhinav.ubercloneapp.dto.EarningsSummaryDto(" +
           "SUM(e.grossFare), SUM(e.platformCommission), SUM(e.netEarning), COUNT(e), " +
           ":startDate, :endDate) " +
           "FROM DriverEarning e WHERE e.driver = :driver " +
           "AND e.createdTime >= :startDate AND e.createdTime < :endDate")
    EarningsSummaryDto getEarningsSummary(@Param("driver") Driver driver,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(e.netEarning), 0) FROM DriverEarning e WHERE e.driver = :driver")
    BigDecimal getTotalNetEarningsByDriver(@Param("driver") Driver driver);
}
