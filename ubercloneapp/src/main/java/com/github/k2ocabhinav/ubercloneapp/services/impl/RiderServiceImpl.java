package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RideDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RideRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RiderDto;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import com.github.k2ocabhinav.ubercloneapp.repositories.RideRequestRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.RiderRepository;
import com.github.k2ocabhinav.ubercloneapp.services.RiderService;
import com.github.k2ocabhinav.ubercloneapp.strategies.DriverMatchingStrategy;
import com.github.k2ocabhinav.ubercloneapp.strategies.RideFareCalculationStrategy;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
public class RiderServiceImpl implements RiderService {

    private final RiderRepository riderRepository;
    private final RideRequestRepository rideRequestRepository;
    private final ModelMapper modelMapper;
    private final RideFareCalculationStrategy rideFareCalculationStrategy;
    private final DriverMatchingStrategy driverMatchingStrategy;

    @Override
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {
        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);

        Double fare = rideFareCalculationStrategy.calculateFare(rideRequest);
        rideRequest.setFare(fare);

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        driverMatchingStrategy.findMatchingDrivers(rideRequest);

        return modelMapper.map(savedRideRequest, RideRequestDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {
        return null;
    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {
        return null;
    }

    @Override
    public RiderDto getMyProfile() {
        return null;
    }

    @Override
    public List<RideDto> getAllMyRides() {
        return List.of();
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider
                .builder()
                .user(user)
                .rating(0.0)
                .build();
        return riderRepository.save(rider);
    }
}
