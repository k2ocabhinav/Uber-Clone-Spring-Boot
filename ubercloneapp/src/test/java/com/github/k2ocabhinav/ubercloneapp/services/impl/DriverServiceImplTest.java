package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RideDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RiderDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus;
import com.github.k2ocabhinav.ubercloneapp.repositories.DriverRepository;
import com.github.k2ocabhinav.ubercloneapp.security.UserPrincipal;
import com.github.k2ocabhinav.ubercloneapp.services.PaymentService;
import com.github.k2ocabhinav.ubercloneapp.services.RatingService;
import com.github.k2ocabhinav.ubercloneapp.services.RideRequestService;
import com.github.k2ocabhinav.ubercloneapp.services.RideService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private RideRequestService rideRequestService;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RideService rideService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RatingService ratingService;

    @Mock
    private com.github.k2ocabhinav.ubercloneapp.services.DriverEarningsService driverEarningsService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    private DriverServiceImpl driverService;

    private Driver testDriver;
    private RideRequest testRideRequest;
    private Ride testRide;

    @BeforeEach
    void setUp() {
        driverService = new DriverServiceImpl(
                rideRequestService, driverRepository, rideService, modelMapper, paymentService, ratingService, driverEarningsService);

        testDriver = Driver.builder()
                .id(1L)
                .vehicleId("VEH123")
                .rating(4.5)
                .available(true)
                .build();

        testRideRequest = RideRequest.builder()
                .id(1L)
                .rideRequestStatus(RideRequestStatus.PENDING)
                .fare(100.0)
                .build();

        testRide = Ride.builder()
                .id(1L)
                .driver(testDriver)
                .rideStatus(RideStatus.CONFIRMED)
                .otp("1234")
                .fare(100.0)
                .paymentMethod(PaymentMethod.CASH)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedDriver() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUserId()).thenReturn(101L);
        when(driverRepository.findByUserId(101L)).thenReturn(Optional.of(testDriver));
        lenient().when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Accept ride should create ride and set driver unavailable")
    void acceptRide_WithPendingRequest_ShouldCreateRideAndSetUnavailable() {
        mockAuthenticatedDriver();
        when(rideRequestService.findRideRequestById(1L)).thenReturn(testRideRequest);
        when(rideService.createNewRide(testRideRequest, testDriver)).thenReturn(testRide);
        when(modelMapper.map(testRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = driverService.acceptRide(1L);

        assertThat(result).isNotNull();
        verify(rideService).createNewRide(testRideRequest, testDriver);
        verify(driverRepository).save(testDriver);
        assertThat(testDriver.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Accept ride with non-pending status should throw exception")
    void acceptRide_WithNonPendingStatus_ShouldThrowException() {
        testRideRequest.setRideRequestStatus(RideRequestStatus.CANCELLED);

        when(rideRequestService.findRideRequestById(1L)).thenReturn(testRideRequest);

        assertThatThrownBy(() -> driverService.acceptRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cannot be accepted");
    }

    @Test
    @DisplayName("Accept ride when driver unavailable should throw exception")
    void acceptRide_WhenDriverUnavailable_ShouldThrowException() {
        mockAuthenticatedDriver();
        testDriver.setAvailable(false);

        when(rideRequestService.findRideRequestById(1L)).thenReturn(testRideRequest);

        assertThatThrownBy(() -> driverService.acceptRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("unavailability");
    }

    @Test
    @DisplayName("Cancel ride should update status and make driver available")
    void cancelRide_WithConfirmedStatus_ShouldCancelAndMakeAvailable() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.CONFIRMED);

        when(rideService.getRideById(1L)).thenReturn(testRide);
        when(rideService.updateRideStatus(testRide, RideStatus.CANCELLED)).thenReturn(testRide);
        when(modelMapper.map(testRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = driverService.cancelRide(1L);

        assertThat(result).isNotNull();
        verify(rideService).updateRideStatus(testRide, RideStatus.CANCELLED);
        verify(driverRepository).save(testDriver);
    }

    @Test
    @DisplayName("Cancel ride by non-owner driver should throw exception")
    void cancelRide_ByNonOwnerDriver_ShouldThrowException() {
        mockAuthenticatedDriver();
        Driver otherDriver = Driver.builder().id(99L).build();
        testRide.setDriver(otherDriver);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> driverService.cancelRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("has not accepted it earlier");
    }

    @Test
    @DisplayName("Cancel ride with invalid status should throw exception")
    void cancelRide_WithInvalidStatus_ShouldThrowException() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.ONGOING);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> driverService.cancelRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    @Test
    @DisplayName("Start ride with valid OTP should start ride and create payment")
    void startRide_WithValidOtp_ShouldStartRideAndCreatePayment() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.CONFIRMED);
        testRide.setStartedAt(null);

        when(rideService.getRideById(1L)).thenReturn(testRide);
        when(rideService.updateRideStatus(testRide, RideStatus.ONGOING)).thenReturn(testRide);
        when(modelMapper.map(testRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = driverService.startRide(1L, "1234");

        assertThat(result).isNotNull();
        assertThat(testRide.getStartedAt()).isNotNull();
        verify(paymentService).createNewPayment(testRide);
        verify(ratingService).createNewRating(testRide);
    }

    @Test
    @DisplayName("Start ride with invalid OTP should throw exception")
    void startRide_WithInvalidOtp_ShouldThrowException() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.CONFIRMED);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> driverService.startRide(1L, "wrong-otp"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Otp is not valid");
    }

    @Test
    @DisplayName("Start ride with invalid status should throw exception")
    void startRide_WithInvalidStatus_ShouldThrowException() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.CANCELLED);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> driverService.startRide(1L, "1234"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("status is not CONFIRMED");
    }

    @Test
    @DisplayName("Start ride by non-owner driver should throw exception")
    void startRide_ByNonOwnerDriver_ShouldThrowException() {
        mockAuthenticatedDriver();
        Driver otherDriver = Driver.builder().id(99L).build();
        testRide.setDriver(otherDriver);
        testRide.setRideStatus(RideStatus.CONFIRMED);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> driverService.startRide(1L, "1234"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("has not accepted it earlier");
    }

    @Test
    @DisplayName("End ride with ongoing status should end ride and process payment")
    void endRide_WithOngoingStatus_ShouldEndRideAndProcessPayment() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.ONGOING);
        testRide.setStartedAt(LocalDateTime.now());

        when(rideService.getRideById(1L)).thenReturn(testRide);
        when(rideService.updateRideStatus(testRide, RideStatus.ENDED)).thenReturn(testRide);
        when(modelMapper.map(testRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = driverService.endRide(1L);

        assertThat(result).isNotNull();
        assertThat(testRide.getEndedAt()).isNotNull();
        assertThat(testDriver.getAvailable()).isTrue();
        verify(paymentService).processPayment(testRide);
    }

    @Test
    @DisplayName("End ride with invalid status should throw exception")
    void endRide_WithInvalidStatus_ShouldThrowException() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.CONFIRMED);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> driverService.endRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("status is not ONGOING");
    }

    @Test
    @DisplayName("End ride by non-owner driver should throw exception")
    void endRide_ByNonOwnerDriver_ShouldThrowException() {
        mockAuthenticatedDriver();
        Driver otherDriver = Driver.builder().id(99L).build();
        testRide.setDriver(otherDriver);
        testRide.setRideStatus(RideStatus.ONGOING);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> driverService.endRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("has not accepted it earlier");
    }

    @Test
    @DisplayName("Rate rider for ended ride should return rider dto")
    void rateRider_ForEndedRide_ShouldReturnRiderDto() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.ENDED);

        when(rideService.getRideById(1L)).thenReturn(testRide);
        when(ratingService.rateRider(testRide, 5)).thenReturn(new RiderDto());

        RiderDto result = driverService.rateRider(1L, 5);

        assertThat(result).isNotNull();
        verify(ratingService).rateRider(testRide, 5);
    }

    @Test
    @DisplayName("Rate rider for non-ended ride should throw exception")
    void rateRider_ForNonEndedRide_ShouldThrowException() {
        mockAuthenticatedDriver();
        testRide.setRideStatus(RideStatus.ONGOING);

        when(rideService.getRideById(1L)).thenReturn(testRide);

        assertThatThrownBy(() -> driverService.rateRider(1L, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("status is not Ended");
    }

    @Test
    @DisplayName("Get my profile should return driver dto")
    void getMyProfile_ShouldReturnDriverDto() {
        mockAuthenticatedDriver();
        when(modelMapper.map(testDriver, DriverDto.class)).thenReturn(new DriverDto());

        DriverDto result = driverService.getMyProfile();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Get all my rides should return paginated rides")
    void getAllMyRides_ShouldReturnPaginatedRides() {
        mockAuthenticatedDriver();
        Page<Ride> ridePage = new PageImpl<>(List.of(testRide));
        when(rideService.getAllRidesOfDriver(testDriver, PageRequest.of(0, 10))).thenReturn(ridePage);

        Page<RideDto> result = driverService.getAllMyRides(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Update driver availability should save and return driver")
    void updateDriverAvailability_ShouldSaveAndReturnDriver() {
        when(driverRepository.save(testDriver)).thenReturn(testDriver);

        Driver result = driverService.updateDriverAvailability(testDriver, false);

        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Create new driver should save and return driver")
    void createNewDriver_ShouldSaveAndReturnDriver() {
        when(driverRepository.save(testDriver)).thenReturn(testDriver);

        Driver result = driverService.createNewDriver(testDriver);

        assertThat(result).isEqualTo(testDriver);
    }
}
