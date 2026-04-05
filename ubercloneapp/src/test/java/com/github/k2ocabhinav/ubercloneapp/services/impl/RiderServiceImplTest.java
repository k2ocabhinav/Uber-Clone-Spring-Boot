package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PointDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RideDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RideRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RiderDto;
import com.github.k2ocabhinav.ubercloneapp.entities.*;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus;
import com.github.k2ocabhinav.ubercloneapp.repositories.RideRequestRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.RiderRepository;
import com.github.k2ocabhinav.ubercloneapp.security.UserPrincipal;
import com.github.k2ocabhinav.ubercloneapp.services.DriverService;
import com.github.k2ocabhinav.ubercloneapp.services.RatingService;
import com.github.k2ocabhinav.ubercloneapp.services.RideService;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideFareCalculationStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.DriverMatchingStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideStrategyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiderServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RideStrategyManager rideStrategyManager;

    @Mock
    private RideRequestRepository rideRequestRepository;

    @Mock
    private RiderRepository riderRepository;

    @Mock
    private RideService rideService;

    @Mock
    private DriverService driverService;

    @Mock
    private RatingService ratingService;

    @Mock
    private RideFareCalculationStrategy fareCalculationStrategy;

    @Mock
    private DriverMatchingStrategy driverMatchingStrategy;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    private RiderServiceImpl riderService;

    private Rider testRider;
    private Ride testRide;
    private RideRequestDto testRideRequestDto;
    private RideRequest testRideRequest;

    @BeforeEach
    void setUp() {
        riderService = new RiderServiceImpl(
                modelMapper,
                rideStrategyManager,
                rideRequestRepository,
                riderRepository,
                rideService,
                driverService,
                ratingService);

        User testUser = User.builder()
                .id(1L)
                .email("rider@test.com")
                .build();

        testRider = Rider.builder()
                .id(1L)
                .user(testUser)
                .rating(4.5)
                .build();

        PointDto pickupLocation = new PointDto(new double[]{40.7128, -74.0060});
        PointDto dropOffLocation = new PointDto(new double[]{40.7580, -73.9855});
        testRideRequestDto = new RideRequestDto();
        testRideRequestDto.setPickupLocation(pickupLocation);
        testRideRequestDto.setDropOffLocation(dropOffLocation);

        testRideRequest = RideRequest.builder()
                .id(1L)
                .rideRequestStatus(RideRequestStatus.PENDING)
                .rider(testRider)
<<<<<<< HEAD
                .fare(BigDecimal.valueOf(100.0))
=======
                .fare(java.math.BigDecimal.valueOf(100.0))
>>>>>>> main
                .build();

        Driver testDriver = Driver.builder()
                .id(1L)
                .build();

        testRide = Ride.builder()
                .id(1L)
                .rider(testRider)
                .driver(testDriver)
                .rideStatus(RideStatus.CONFIRMED)
                .fare(100.0)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedRider() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUserId()).thenReturn(101L);
        when(riderRepository.findByUserId(101L)).thenReturn(Optional.of(testRider));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Request ride should create ride request with fare and return")
    void requestRide_ShouldCreateRideRequestWithFare() {
        mockAuthenticatedRider();
        when(modelMapper.map(testRideRequestDto, RideRequest.class)).thenReturn(testRideRequest);
        when(rideStrategyManager.rideFareCalculationStrategy()).thenReturn(fareCalculationStrategy);
        when(fareCalculationStrategy.calculateFare(testRideRequest)).thenReturn(150.0);
        when(rideRequestRepository.save(testRideRequest)).thenReturn(testRideRequest);
        when(rideStrategyManager.driverMatchingStrategy(testRider.getRating())).thenReturn(driverMatchingStrategy);
        when(driverMatchingStrategy.findMatchingDrivers(testRideRequest)).thenReturn(List.of());
        when(modelMapper.map(testRideRequest, RideRequestDto.class)).thenReturn(testRideRequestDto);

        RideRequestDto result = riderService.requestRide(testRideRequestDto);

        assertThat(result).isNotNull();
        verify(rideRequestRepository).save(testRideRequest);
<<<<<<< HEAD
        assertThat(testRideRequest.getFare()).isEqualByComparingTo(BigDecimal.valueOf(150.0));
=======
        assertThat(testRideRequest.getFare()).isEqualTo(java.math.BigDecimal.valueOf(150.0));
>>>>>>> main
    }

    @Test
    @DisplayName("Cancel ride by owner should cancel and make driver available")
    void cancelRide_ByOwner_ShouldCancelAndMakeDriverAvailable() {
        mockAuthenticatedRider();
        when(rideService.getRideById(1L)).thenReturn(testRide);
        when(rideService.updateRideStatus(testRide, RideStatus.CANCELLED)).thenReturn(testRide);
        when(modelMapper.map(testRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = riderService.cancelRide(1L);

        assertThat(result).isNotNull();
        verify(rideService).updateRideStatus(testRide, RideStatus.CANCELLED);
        verify(driverService).updateDriverAvailability(testRide.getDriver(), true);
    }

    @Test
    @DisplayName("Cancel ride by non-owner should throw exception")
    void cancelRide_ByNonOwner_ShouldThrowException() {
        mockAuthenticatedRider();
        Rider otherRider = Rider.builder().id(99L).build();
        testRide.setRider(otherRider);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> riderService.cancelRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not own this ride");
    }

    @Test
    @DisplayName("Cancel ride with invalid status should throw exception")
    void cancelRide_WithInvalidStatus_ShouldThrowException() {
        mockAuthenticatedRider();
        testRide.setRideStatus(RideStatus.ONGOING);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> riderService.cancelRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    @Test
    @DisplayName("Rate driver for ended ride should return driver dto")
    void rateDriver_ForEndedRide_ShouldReturnDriverDto() {
        mockAuthenticatedRider();
        testRide.setRideStatus(RideStatus.ENDED);

        when(rideService.getRideById(1L)).thenReturn(testRide);
        when(ratingService.rateDriver(testRide, 5)).thenReturn(new DriverDto());

        DriverDto result = riderService.rateDriver(1L, 5);

        assertThat(result).isNotNull();
        verify(ratingService).rateDriver(testRide, 5);
    }

    @Test
    @DisplayName("Rate driver for non-ended ride should throw exception")
    void rateDriver_ForNonEndedRide_ShouldThrowException() {
        mockAuthenticatedRider();
        testRide.setRideStatus(RideStatus.ONGOING);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> riderService.rateDriver(1L, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("status is not Ended");
    }

    @Test
    @DisplayName("Rate driver by non-owner should throw exception")
    void rateDriver_ByNonOwner_ShouldThrowException() {
        mockAuthenticatedRider();
        Rider otherRider = Rider.builder().id(99L).build();
        testRide.setRider(otherRider);
        testRide.setRideStatus(RideStatus.ENDED);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> riderService.rateDriver(1L, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not the owner");
    }

    @Test
    @DisplayName("Get my profile should return rider dto")
    void getMyProfile_ShouldReturnRiderDto() {
        mockAuthenticatedRider();
        when(modelMapper.map(testRider, RiderDto.class)).thenReturn(new RiderDto());

        RiderDto result = riderService.getMyProfile();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Get all my rides should return paginated rides")
    void getAllMyRides_ShouldReturnPaginatedRides() {
        mockAuthenticatedRider();
        Page<Ride> ridePage = new PageImpl<>(List.of(testRide));
        when(rideService.getAllRidesOfRider(testRider, PageRequest.of(0, 10))).thenReturn(ridePage);

        Page<RideDto> result = riderService.getAllMyRides(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Create new rider should save and return rider")
    void createNewRider_ShouldSaveAndReturnRider() {
        User testUser = User.builder().id(1L).build();

        when(riderRepository.save(any(Rider.class))).thenAnswer(invocation -> {
            Rider r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        Rider result = riderService.createNewRider(testUser);

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(0.0);
        verify(riderRepository).save(any(Rider.class));
    }
}
