package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.configs.ScheduledRideConfig;
import com.github.k2ocabhinav.ubercloneapp.dto.RideRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.ScheduledRideDto;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.repositories.RideRequestRepository;
import com.github.k2ocabhinav.ubercloneapp.services.RiderService;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideFareCalculationStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.DriverMatchingStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideStrategyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledRideServiceImplTest {

    @Mock private RideRequestRepository rideRequestRepository;
    @Mock private RiderService riderService;
    @Mock private RideStrategyManager rideStrategyManager;
    @Mock private RideFareCalculationStrategy fareCalculationStrategy;
    @Mock private DriverMatchingStrategy driverMatchingStrategy;
    @Mock private ModelMapper modelMapper;

    private ScheduledRideConfig scheduledRideConfig;
    private ScheduledRideServiceImpl scheduledRideService;

    private User testUser;
    private Rider testRider;
    private RideRequest testRideRequest;

    @BeforeEach
    void setUp() {
        scheduledRideConfig = new ScheduledRideConfig();
        scheduledRideConfig.setMinAdvanceMinutes(30);
        scheduledRideConfig.setMaxAdvanceDays(7);
        scheduledRideConfig.setDispatchWindowMinutes(15);

        scheduledRideService = new ScheduledRideServiceImpl(
                rideRequestRepository, riderService, rideStrategyManager,
                scheduledRideConfig, modelMapper);

        testUser = User.builder().id(1L).email("rider@test.com").build();
        testRider = Rider.builder().id(1L).user(testUser).rating(4.5).build();
        testRideRequest = RideRequest.builder()
                .id(1L)
                .rider(testRider)
                .rideRequestStatus(RideRequestStatus.SCHEDULED)
                .scheduledTime(LocalDateTime.now(ZoneId.of("UTC")).plusHours(2))
                .fare(new BigDecimal("150.00"))
                .build();
    }

    @Nested
    @DisplayName("Schedule Ride")
    class ScheduleRide {

        @Test
        @DisplayName("Should schedule a ride with valid future time")
        void shouldScheduleRideSuccess() {
            LocalDateTime scheduledTime = LocalDateTime.now(ZoneId.of("UTC")).plusHours(2);
            RideRequestDto requestDto = new RideRequestDto();
            requestDto.setScheduledTime(scheduledTime);
            requestDto.setPaymentMethod(PaymentMethod.WALLET);

            when(riderService.getCurrentRider()).thenReturn(testRider);
            when(modelMapper.map(requestDto, RideRequest.class)).thenReturn(testRideRequest);
            when(rideStrategyManager.rideFareCalculationStrategy()).thenReturn(fareCalculationStrategy);
            when(fareCalculationStrategy.calculateFare(any())).thenReturn(BigDecimal.valueOf(150.0));
            when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(testRideRequest);
            when(modelMapper.map(testRideRequest, ScheduledRideDto.class)).thenReturn(new ScheduledRideDto());

            ScheduledRideDto result = scheduledRideService.scheduleRide(requestDto);

            assertThat(result).isNotNull();

            ArgumentCaptor<RideRequest> captor = ArgumentCaptor.forClass(RideRequest.class);
            verify(rideRequestRepository).save(captor.capture());
            assertThat(captor.getValue().getRideRequestStatus()).isEqualTo(RideRequestStatus.SCHEDULED);
        }

        @Test
        @DisplayName("Should reject scheduling with null time")
        void shouldRejectNullScheduledTime() {
            RideRequestDto requestDto = new RideRequestDto();
            requestDto.setScheduledTime(null);

            assertThatThrownBy(() -> scheduledRideService.scheduleRide(requestDto))
                    .isInstanceOf(RuntimeConflictException.class)
                    .hasMessageContaining("Scheduled time is required");
        }

        @Test
        @DisplayName("Should reject scheduling less than 30 minutes ahead")
        void shouldRejectTooSoonScheduledTime() {
            RideRequestDto requestDto = new RideRequestDto();
            requestDto.setScheduledTime(LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(10));

            assertThatThrownBy(() -> scheduledRideService.scheduleRide(requestDto))
                    .isInstanceOf(RuntimeConflictException.class)
                    .hasMessageContaining("at least 30 minutes");
        }

        @Test
        @DisplayName("Should reject scheduling more than 7 days ahead")
        void shouldRejectTooFarScheduledTime() {
            RideRequestDto requestDto = new RideRequestDto();
            requestDto.setScheduledTime(LocalDateTime.now(ZoneId.of("UTC")).plusDays(8));

            assertThatThrownBy(() -> scheduledRideService.scheduleRide(requestDto))
                    .isInstanceOf(RuntimeConflictException.class)
                    .hasMessageContaining("7 days");
        }
    }

    @Nested
    @DisplayName("Cancel Scheduled Ride")
    class CancelScheduledRide {

        @Test
        @DisplayName("Should cancel a scheduled ride owned by the current rider")
        void shouldCancelScheduledRide() {
            when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(testRideRequest));
            when(riderService.getCurrentRider()).thenReturn(testRider);
            when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(testRideRequest);
            when(modelMapper.map(any(RideRequest.class), eq(ScheduledRideDto.class)))
                    .thenReturn(new ScheduledRideDto());

            scheduledRideService.cancelScheduledRide(1L);

            ArgumentCaptor<RideRequest> captor = ArgumentCaptor.forClass(RideRequest.class);
            verify(rideRequestRepository).save(captor.capture());
            assertThat(captor.getValue().getRideRequestStatus()).isEqualTo(RideRequestStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should throw when cancelling non-SCHEDULED ride request")
        void shouldRejectCancelNonScheduledRide() {
            testRideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
            when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(testRideRequest));
            when(riderService.getCurrentRider()).thenReturn(testRider);

            assertThatThrownBy(() -> scheduledRideService.cancelScheduledRide(1L))
                    .isInstanceOf(RuntimeConflictException.class)
                    .hasMessageContaining("not in SCHEDULED status");
        }

        @Test
        @DisplayName("Should throw when ride request not found")
        void shouldThrowNotFound() {
            when(rideRequestRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> scheduledRideService.cancelScheduledRide(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Reschedule Ride")
    class RescheduleRide {

        @Test
        @DisplayName("Should reschedule and recalculate fare")
        void shouldRescheduleAndRecalculateFare() {
            LocalDateTime newTime = LocalDateTime.now(ZoneId.of("UTC")).plusHours(5);
            when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(testRideRequest));
            when(riderService.getCurrentRider()).thenReturn(testRider);
            when(rideStrategyManager.rideFareCalculationStrategy()).thenReturn(fareCalculationStrategy);
            when(fareCalculationStrategy.calculateFare(any())).thenReturn(BigDecimal.valueOf(180.0));
            when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(testRideRequest);
            when(modelMapper.map(any(RideRequest.class), eq(ScheduledRideDto.class)))
                    .thenReturn(new ScheduledRideDto());

            scheduledRideService.rescheduleRide(1L, newTime);

            ArgumentCaptor<RideRequest> captor = ArgumentCaptor.forClass(RideRequest.class);
            verify(rideRequestRepository).save(captor.capture());
            assertThat(captor.getValue().getScheduledTime()).isEqualTo(newTime);
            verify(fareCalculationStrategy).calculateFare(any());
        }
    }

    @Nested
    @DisplayName("Dispatch Scheduled Rides")
    class DispatchScheduledRides {

        @Test
        @DisplayName("Should dispatch due rides by changing status to PENDING")
        void shouldDispatchDueRides() {
            when(rideRequestRepository.findDueScheduledRides(
                    eq(RideRequestStatus.SCHEDULED), any(LocalDateTime.class)))
                    .thenReturn(List.of(testRideRequest));
            when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(testRideRequest);
            when(rideStrategyManager.driverMatchingStrategy(anyDouble())).thenReturn(driverMatchingStrategy);
            when(driverMatchingStrategy.findMatchingDrivers(any())).thenReturn(Collections.emptyList());

            int dispatched = scheduledRideService.dispatchScheduledRides();

            assertThat(dispatched).isEqualTo(1);
            ArgumentCaptor<RideRequest> captor = ArgumentCaptor.forClass(RideRequest.class);
            verify(rideRequestRepository).save(captor.capture());
            assertThat(captor.getValue().getRideRequestStatus()).isEqualTo(RideRequestStatus.PENDING);
        }

        @Test
        @DisplayName("Should return 0 when no rides are due")
        void shouldReturnZeroWhenNoDueRides() {
            when(rideRequestRepository.findDueScheduledRides(
                    eq(RideRequestStatus.SCHEDULED), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            int dispatched = scheduledRideService.dispatchScheduledRides();

            assertThat(dispatched).isEqualTo(0);
            verify(rideRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle dispatch failure gracefully without blocking others")
        void shouldHandleDispatchFailureGracefully() {
            RideRequest rideRequest2 = RideRequest.builder()
                    .id(2L).rider(testRider).rideRequestStatus(RideRequestStatus.SCHEDULED)
                    .scheduledTime(LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(5))
                    .fare(new BigDecimal("100.00")).build();

            when(rideRequestRepository.findDueScheduledRides(
                    eq(RideRequestStatus.SCHEDULED), any(LocalDateTime.class)))
                    .thenReturn(List.of(testRideRequest, rideRequest2));

            // First ride dispatch fails
            when(rideRequestRepository.save(testRideRequest)).thenThrow(new RuntimeException("DB error"));
            when(rideRequestRepository.save(rideRequest2)).thenReturn(rideRequest2);
            when(rideStrategyManager.driverMatchingStrategy(anyDouble())).thenReturn(driverMatchingStrategy);
            when(driverMatchingStrategy.findMatchingDrivers(any())).thenReturn(Collections.emptyList());

            int dispatched = scheduledRideService.dispatchScheduledRides();

            assertThat(dispatched).isEqualTo(1);
        }
    }
}
