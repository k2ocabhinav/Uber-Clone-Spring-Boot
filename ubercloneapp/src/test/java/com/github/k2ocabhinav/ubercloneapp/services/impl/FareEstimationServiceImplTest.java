package com.github.k2ocabhinav.ubercloneapp.services.impl;

<<<<<<< HEAD
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateDto;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PointDto;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideFareCalculationStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideStrategyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
=======
import com.github.k2ocabhinav.ubercloneapp.configs.FareConfig;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateDto;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PointDto;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.services.DistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
>>>>>>> 269d8a7 (fix: update tests to use BigDecimal for fare)

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FareEstimationServiceImplTest {

    @Mock
    private DistanceService distanceService;

<<<<<<< HEAD
    @Mock
    private RideStrategyManager rideStrategyManager;

    @Mock
    private RideFareCalculationStrategy fareCalculationStrategy;

    private ModelMapper modelMapper;

    @InjectMocks
    private FareEstimationServiceImpl fareEstimationService;

    private FareEstimateRequestDto requestDto;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @BeforeEach
    void setUp() {
        modelMapper = new com.github.k2ocabhinav.ubercloneapp.configs.MapperConfig().modelMapper();
        fareEstimationService = new FareEstimationServiceImpl(distanceService, rideStrategyManager, modelMapper);
        
        requestDto = new FareEstimateRequestDto();
        requestDto.setPickupLocation(new PointDto(new double[]{77.123, 28.123}));
        requestDto.setDropOffLocation(new PointDto(new double[]{77.456, 28.456}));
    }

    @Test
    void estimateFare_ShouldReturnValidEstimate() {
        // Arrange
        org.mockito.Mockito.lenient().when(distanceService.calculateDistanceAndDuration(any(Point.class), any(Point.class)))
                .thenReturn(new double[]{10.5, 25.0});
        when(rideStrategyManager.rideFareCalculationStrategy()).thenReturn(fareCalculationStrategy);
        when(fareCalculationStrategy.calculateFare(any(RideRequest.class))).thenReturn(150.0);

        // Act
        FareEstimateDto result = fareEstimationService.estimateFare(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEstimatedFare()).isEqualByComparingTo(BigDecimal.valueOf(150.0));
        assertThat(result.getDistanceKm()).isEqualTo(10.5);
        assertThat(result.getDurationMinutes()).isEqualTo(25.0);
=======
    private FareConfig fareConfig;
    private FareEstimationServiceImpl fareEstimationService;

    @BeforeEach
    void setUp() {
        fareConfig = new FareConfig();
        fareConfig.setBaseFare(5.0);
        fareConfig.setPerKmRate(10.0);
        fareConfig.setSurgeStartHour(18);
        fareConfig.setSurgeEndHour(21);
        fareConfig.setSurgeMultiplier(2.0);
        fareEstimationService = new FareEstimationServiceImpl(distanceService, fareConfig);
    }

    @Test
    void estimateFare_returnsFareWithCorrectCalculation() {
        when(distanceService.calculateDistanceAndDuration(any(), any()))
                .thenReturn(new double[]{10.0, 15.0});

        FareEstimateRequestDto request = new FareEstimateRequestDto();
        request.setPickupLocation(new PointDto(new double[]{-122.4194, 37.7749}));
        request.setDropOffLocation(new PointDto(new double[]{-122.2711, 37.8044}));
        request.setPaymentMethod(PaymentMethod.WALLET);

        FareEstimateDto result = fareEstimationService.estimateFare(request);

        assertThat(result.getEstimatedFare()).isEqualByComparingTo("105.00");
        assertThat(result.getDistance()).isEqualTo(10.0);
        assertThat(result.getDuration()).isEqualTo(15.0);
    }

    @Test
    void estimateFare_includesSurgeMultiplierWhenActive() {
        when(distanceService.calculateDistanceAndDuration(any(), any()))
                .thenReturn(new double[]{10.0, 15.0});

        FareEstimateRequestDto request = new FareEstimateRequestDto();
        request.setPickupLocation(new PointDto(new double[]{-122.4194, 37.7749}));
        request.setDropOffLocation(new PointDto(new double[]{-122.2711, 37.8044}));
        request.setPaymentMethod(PaymentMethod.CASH);

        FareEstimateDto result = fareEstimationService.estimateFare(request);

        if (result.getSurgeActive()) {
            assertThat(result.getSurgeMultiplier()).isEqualTo(2.0);
            assertThat(result.getEstimatedFare().doubleValue()).isGreaterThan(105.0);
        } else {
            assertThat(result.getSurgeMultiplier()).isEqualTo(1.0);
            assertThat(result.getEstimatedFare()).isEqualByComparingTo("105.00");
        }
    }

    @Test
    void estimateFare_setsEstimatedPickupTime() {
        when(distanceService.calculateDistanceAndDuration(any(), any()))
                .thenReturn(new double[]{10.0, 15.0});

        FareEstimateRequestDto request = new FareEstimateRequestDto();
        request.setPickupLocation(new PointDto(new double[]{-122.4194, 37.7749}));
        request.setDropOffLocation(new PointDto(new double[]{-122.2711, 37.8044}));
        request.setPaymentMethod(PaymentMethod.WALLET);

        FareEstimateDto result = fareEstimationService.estimateFare(request);

        assertThat(result.getEstimatedPickupTime()).isNotNull();
    }

    @Test
    void estimateFare_setsPromoDiscountStubToZero() {
        when(distanceService.calculateDistanceAndDuration(any(), any()))
                .thenReturn(new double[]{10.0, 15.0});

        FareEstimateRequestDto request = new FareEstimateRequestDto();
        request.setPickupLocation(new PointDto(new double[]{-122.4194, 37.7749}));
        request.setDropOffLocation(new PointDto(new double[]{-122.2711, 37.8044}));
        request.setPaymentMethod(PaymentMethod.WALLET);

        FareEstimateDto result = fareEstimationService.estimateFare(request);

        assertThat(result.getPromoDiscountStub()).isEqualTo(0.0);
>>>>>>> 269d8a7 (fix: update tests to use BigDecimal for fare)
    }
}
