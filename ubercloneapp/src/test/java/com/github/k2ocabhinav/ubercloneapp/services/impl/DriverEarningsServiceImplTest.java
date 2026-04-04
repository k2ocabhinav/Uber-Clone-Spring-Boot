package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.configs.PlatformConfig;
import com.github.k2ocabhinav.ubercloneapp.dto.EarningsSummaryDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.DriverEarning;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.repositories.DriverEarningRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.DriverRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.PayoutRequestRepository;
import com.github.k2ocabhinav.ubercloneapp.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverEarningsServiceImplTest {

    @Mock
    private DriverEarningRepository driverEarningRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private PayoutRequestRepository payoutRequestRepository;

    @Mock
    private PlatformConfig platformConfig;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DriverEarningsServiceImpl driverEarningsService;

    private Driver driver;

    @BeforeEach
    void setUp() {
        driver = new Driver();
        driver.setId(1L);
    }

    private void mockSecurityContext() {
        UserPrincipal principal = new UserPrincipal(1L, "user@test.com", "password");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(driverRepository.findByUserId(1L)).thenReturn(Optional.of(driver));
    }

    @Test
    void createEarningRecord_ShouldCalculateAndSaveProperly() {
        Ride ride = new Ride();
        ride.setFare(100.0);
        ride.setDriver(driver);

        when(platformConfig.getCommissionRate()).thenReturn(0.30);

        driverEarningsService.createEarningRecord(ride);

        verify(driverEarningRepository).save(argThat(earning -> 
            earning.getGrossFare() == 100.0 &&
            earning.getPlatformCommission() == 30.0 &&
            earning.getNetEarning() == 70.0 &&
            earning.getDriver().equals(driver)
        ));
    }

    @Test
    void getDailySummary_ShouldReturnSummary() {
        mockSecurityContext();

        LocalDate today = LocalDate.now();
        EarningsSummaryDto expectedSummary = new EarningsSummaryDto(100.0, 30.0, 70.0, 1L, "DAILY", today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        when(driverEarningRepository.getEarningsSummary(eq(driver), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedSummary);

        EarningsSummaryDto result = driverEarningsService.getDailySummary(today);

        assertThat(result).isNotNull();
        assertThat(result.getTotalGross()).isEqualTo(100.0);
        assertThat(result.getPeriod()).isEqualTo("DAILY");
    }

    @Test
    void getDailySummary_ShouldReturnEmpty_WhenNoRides() {
        mockSecurityContext();

        LocalDate today = LocalDate.now();
        when(driverEarningRepository.getEarningsSummary(eq(driver), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(null);

        EarningsSummaryDto result = driverEarningsService.getDailySummary(today);

        assertThat(result).isNotNull();
        assertThat(result.getTotalGross()).isEqualTo(0.0);
        assertThat(result.getRideCount()).isEqualTo(0L);
        assertThat(result.getPeriod()).isEqualTo("DAILY");
    }
}
