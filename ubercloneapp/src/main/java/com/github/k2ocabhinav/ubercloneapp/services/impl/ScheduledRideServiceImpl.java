package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.configs.ScheduledRideConfig;
import com.github.k2ocabhinav.ubercloneapp.dto.RideRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.ScheduledRideDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.repositories.RideRequestRepository;
import com.github.k2ocabhinav.ubercloneapp.services.RiderService;
import com.github.k2ocabhinav.ubercloneapp.services.ScheduledRideService;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledRideServiceImpl implements ScheduledRideService {

    private static final ZoneId UTC = ZoneId.of("UTC");

    private final RideRequestRepository rideRequestRepository;
    private final RiderService riderService;
    private final RideStrategyManager rideStrategyManager;
    private final ScheduledRideConfig scheduledRideConfig;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ScheduledRideDto scheduleRide(RideRequestDto rideRequestDto) {
        LocalDateTime scheduledTime = rideRequestDto.getScheduledTime();
        validateScheduledTime(scheduledTime);

        Rider rider = riderService.getCurrentRider();

        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
        rideRequest.setRider(rider);
        rideRequest.setRideRequestStatus(RideRequestStatus.SCHEDULED);
        rideRequest.setScheduledTime(scheduledTime);

        BigDecimal fare = BigDecimal.valueOf(rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest));
        rideRequest.setFare(fare);

        RideRequest saved = rideRequestRepository.save(rideRequest);
        log.info("Scheduled ride created: id={}, scheduledTime={}", saved.getId(), scheduledTime);

        return toScheduledRideDto(saved);
    }

    @Override
    @Transactional
    public ScheduledRideDto cancelScheduledRide(Long rideRequestId) {
        RideRequest rideRequest = findOwnedScheduledRequest(rideRequestId);

        rideRequest.setRideRequestStatus(RideRequestStatus.CANCELLED);
        RideRequest saved = rideRequestRepository.save(rideRequest);
        log.info("Scheduled ride cancelled: id={}", rideRequestId);

        return toScheduledRideDto(saved);
    }

    @Override
    @Transactional
    public ScheduledRideDto rescheduleRide(Long rideRequestId, LocalDateTime newScheduledTime) {
        validateScheduledTime(newScheduledTime);

        RideRequest rideRequest = findOwnedScheduledRequest(rideRequestId);
        rideRequest.setScheduledTime(newScheduledTime);

        BigDecimal fare = BigDecimal.valueOf(rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest));
        rideRequest.setFare(fare);

        RideRequest saved = rideRequestRepository.save(rideRequest);
        log.info("Scheduled ride rescheduled: id={}, newTime={}", rideRequestId, newScheduledTime);

        return toScheduledRideDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduledRideDto> getMyScheduledRides(PageRequest pageRequest) {
        Rider rider = riderService.getCurrentRider();
        return rideRequestRepository
                .findByRiderAndRideRequestStatusOrderByScheduledTimeAsc(rider, RideRequestStatus.SCHEDULED, pageRequest)
                .map(this::toScheduledRideDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduledRideDto> getAllScheduledRides(PageRequest pageRequest) {
        return rideRequestRepository
                .findByRideRequestStatusOrderByScheduledTimeAsc(RideRequestStatus.SCHEDULED, pageRequest)
                .map(this::toScheduledRideDto);
    }

    @Override
    @Transactional
    public int dispatchScheduledRides() {
        LocalDateTime dispatchBefore = LocalDateTime.now(UTC)
                .plusMinutes(scheduledRideConfig.getDispatchWindowMinutes());

        List<RideRequest> dueRides = rideRequestRepository
                .findDueScheduledRides(RideRequestStatus.SCHEDULED, dispatchBefore);

        int dispatched = 0;
        for (RideRequest rideRequest : dueRides) {
            try {
                rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
                rideRequestRepository.save(rideRequest);

                Rider rider = rideRequest.getRider();
                List<Driver> matchedDrivers = rideStrategyManager
                        .driverMatchingStrategy(rider.getRating())
                        .findMatchingDrivers(rideRequest);

                log.info("Dispatched scheduled ride: id={}, matchedDrivers={}",
                        rideRequest.getId(), matchedDrivers.size());
                dispatched++;
            } catch (Exception e) {
                log.error("Failed to dispatch scheduled ride id={}: {}",
                        rideRequest.getId(), e.getMessage(), e);
            }
        }

        if (dispatched > 0) {
            log.info("Scheduled ride dispatch complete: dispatched={}/{}", dispatched, dueRides.size());
        }
        return dispatched;
    }

    private void validateScheduledTime(LocalDateTime scheduledTime) {
        if (scheduledTime == null) {
            throw new RuntimeConflictException("Scheduled time is required");
        }

        LocalDateTime now = LocalDateTime.now(UTC);
        LocalDateTime minTime = now.plusMinutes(scheduledRideConfig.getMinAdvanceMinutes());
        LocalDateTime maxTime = now.plusDays(scheduledRideConfig.getMaxAdvanceDays());

        if (scheduledTime.isBefore(minTime)) {
            throw new RuntimeConflictException(
                    "Scheduled time must be at least " + scheduledRideConfig.getMinAdvanceMinutes() +
                    " minutes from now");
        }

        if (scheduledTime.isAfter(maxTime)) {
            throw new RuntimeConflictException(
                    "Scheduled time cannot be more than " + scheduledRideConfig.getMaxAdvanceDays() +
                    " days from now");
        }
    }

    private RideRequest findOwnedScheduledRequest(Long rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ride request not found with id: " + rideRequestId));

        Rider rider = riderService.getCurrentRider();
        if (!rider.equals(rideRequest.getRider())) {
            throw new RuntimeConflictException("You do not own this ride request");
        }

        if (rideRequest.getRideRequestStatus() != RideRequestStatus.SCHEDULED) {
            throw new RuntimeConflictException(
                    "Ride request is not in SCHEDULED status, current status: " +
                    rideRequest.getRideRequestStatus());
        }

        return rideRequest;
    }

    private ScheduledRideDto toScheduledRideDto(RideRequest rideRequest) {
        ScheduledRideDto dto = modelMapper.map(rideRequest, ScheduledRideDto.class);
        dto.setStatus(rideRequest.getRideRequestStatus());
        return dto;
    }
}
