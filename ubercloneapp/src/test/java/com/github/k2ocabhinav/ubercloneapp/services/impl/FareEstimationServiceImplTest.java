package com.github.k2ocabhinav.ubercloneapp.services.impl;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FareEstimationServiceImplTest {

    @Mock
    private DistanceService distanceService;

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
    }
}
