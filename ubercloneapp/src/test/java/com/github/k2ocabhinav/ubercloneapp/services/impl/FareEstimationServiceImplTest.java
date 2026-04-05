package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.configs.FareConfig;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateDto;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PointDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PromoCodeResultDto;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import com.github.k2ocabhinav.ubercloneapp.services.PromoCodeService;
import com.github.k2ocabhinav.ubercloneapp.services.RiderService;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideFareCalculationStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideStrategyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareEstimationServiceImplTest {

    @Mock
    private DistanceService distanceService;

    @Mock
    private RideStrategyManager rideStrategyManager;

    @Mock
    private RideFareCalculationStrategy fareCalculationStrategy;

    @Mock
    private PromoCodeService promoCodeService;

    @Mock
    private RiderService riderService;

    private FareConfig fareConfig;
    private ModelMapper modelMapper;
    private FareEstimationServiceImpl fareEstimationService;

    private FareEstimateRequestDto requestDto;
    private User testUser;
    private Rider testRider;

    @BeforeEach
    void setUp() {
        fareConfig = new FareConfig();
        fareConfig.setPerKmRate(10.0);
        fareConfig.setSurgeMultiplier(1.5);
        
        modelMapper = new com.github.k2ocabhinav.ubercloneapp.configs.MapperConfig().modelMapper();
        fareEstimationService = new FareEstimationServiceImpl(
                distanceService, rideStrategyManager, promoCodeService, 
                riderService, fareConfig, modelMapper);
        
        requestDto = new FareEstimateRequestDto();
        requestDto.setPickupLocation(new PointDto(new double[]{77.123, 28.123}));
        requestDto.setDropOffLocation(new PointDto(new double[]{77.456, 28.456}));
        
        testUser = User.builder().id(1L).build();
        testRider = Rider.builder().id(1L).user(testUser).build();
    }

    @Test
    @DisplayName("Estimate fare should return valid estimate without promo")
    void estimateFare_WithoutPromo_ShouldReturnValidEstimate() {
        // Arrange
        when(distanceService.calculateDistanceAndDuration(any(Point.class), any(Point.class)))
                .thenReturn(new double[]{10.5, 25.0});
        when(rideStrategyManager.rideFareCalculationStrategy()).thenReturn(fareCalculationStrategy);
        when(fareCalculationStrategy.calculateFare(any(RideRequest.class))).thenReturn(BigDecimal.valueOf(150.0));
        when(promoCodeService.getActivePromoCodes()).thenReturn(Collections.emptyList());

        // Act
        FareEstimateDto result = fareEstimationService.estimateFare(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBaseFare()).isEqualByComparingTo(BigDecimal.valueOf(150.0));
        assertThat(result.getEstimatedFare()).isEqualByComparingTo(BigDecimal.valueOf(150.0));
        assertThat(result.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getDistanceKm()).isEqualTo(10.5);
        assertThat(result.getDurationMinutes()).isEqualTo(25.0);
        assertThat(result.getSurgeMultiplier()).isEqualTo(1.5);
        verify(promoCodeService, never()).validateAndApplyPromo(anyString(), any(), any());
    }

    @Test
    @DisplayName("Estimate fare should apply valid promo code")
    void estimateFare_WithValidPromo_ShouldApplyDiscount() {
        // Arrange
        requestDto.setPromoCode("SAVE50");
        when(distanceService.calculateDistanceAndDuration(any(Point.class), any(Point.class)))
                .thenReturn(new double[]{10.0, 20.0});
        when(rideStrategyManager.rideFareCalculationStrategy()).thenReturn(fareCalculationStrategy);
        when(fareCalculationStrategy.calculateFare(any(RideRequest.class))).thenReturn(BigDecimal.valueOf(100.0));
        
        when(riderService.getCurrentRider()).thenReturn(testRider);
        
        PromoCodeResultDto promoResult = new PromoCodeResultDto();
        promoResult.setValid(true);
        promoResult.setDiscountAmount(BigDecimal.valueOf(50.0));
        promoResult.setDiscountedFare(BigDecimal.valueOf(50.0));
        
        when(promoCodeService.validateAndApplyPromo(eq("SAVE50"), eq(testUser), any(BigDecimal.class)))
                .thenReturn(promoResult);
        when(promoCodeService.getActivePromoCodes()).thenReturn(Collections.emptyList());

        // Act
        FareEstimateDto result = fareEstimationService.estimateFare(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBaseFare()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
        assertThat(result.getDiscountAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.0));
        assertThat(result.getEstimatedFare()).isEqualByComparingTo(BigDecimal.valueOf(50.0));
    }
}
