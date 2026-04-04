package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.DriverDto;
import com.github.k2ocabhinav.ubercloneapp.dto.RiderDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Rating;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.exceptions.RuntimeConflictException;
import com.github.k2ocabhinav.ubercloneapp.repositories.DriverRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.RatingRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.RiderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RiderRepository riderRepository;

    @Mock
    private ModelMapper modelMapper;

    private RatingServiceImpl ratingService;

    private Driver testDriver;
    private Rider testRider;
    private Ride testRide;
    private Rating testRating;

    @BeforeEach
    void setUp() {
        ratingService = new RatingServiceImpl(ratingRepository, driverRepository, riderRepository, modelMapper);

        User driverUser = User.builder().id(1L).email("driver@test.com").build();
        testDriver = Driver.builder()
                .id(1L)
                .user(driverUser)
                .rating(4.5)
                .build();

        User riderUser = User.builder().id(2L).email("rider@test.com").build();
        testRider = Rider.builder()
                .id(1L)
                .user(riderUser)
                .rating(4.0)
                .build();

        testRide = Ride.builder()
                .id(1L)
                .driver(testDriver)
                .rider(testRider)
                .build();

        testRating = Rating.builder()
                .id(1L)
                .ride(testRide)
                .driver(testDriver)
                .rider(testRider)
                .build();
    }

    @Test
    @DisplayName("Rate driver should update rating and calculate new average")
    void rateDriver_ShouldUpdateRatingAndCalculateAverage() {
        Rating ratingWithDriverRating = Rating.builder()
                .id(1L)
                .ride(testRide)
                .driver(testDriver)
                .driverRating(null)
                .build();

        when(ratingRepository.findByRide(testRide)).thenReturn(Optional.of(ratingWithDriverRating));
        when(ratingRepository.findByDriver(testDriver)).thenReturn(List.of(
                Rating.builder().driverRating(4).build(),
                Rating.builder().driverRating(5).build()
        ));
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(modelMapper.map(testDriver, DriverDto.class)).thenReturn(new DriverDto());

        DriverDto result = ratingService.rateDriver(testRide, 5);

        assertThat(result).isNotNull();
        verify(ratingRepository).save(ratingWithDriverRating);
        assertThat(ratingWithDriverRating.getDriverRating()).isEqualTo(5);
        assertThat(testDriver.getRating()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("Rate driver when already rated should throw exception")
    void rateDriver_WhenAlreadyRated_ShouldThrowException() {
        Rating ratingWithDriverRating = Rating.builder()
                .id(1L)
                .driverRating(4)
                .build();

        when(ratingRepository.findByRide(testRide)).thenReturn(Optional.of(ratingWithDriverRating));

        assertThatThrownBy(() -> ratingService.rateDriver(testRide, 5))
                .isInstanceOf(RuntimeConflictException.class)
                .hasMessageContaining("already been rated");
    }

    @Test
    @DisplayName("Rate driver with no existing ratings should calculate average correctly")
    void rateDriver_WithNoExistingRatings_ShouldCalculateCorrectly() {
        Rating ratingWithDriverRating = Rating.builder()
                .id(1L)
                .driverRating(null)
                .build();

        when(ratingRepository.findByRide(testRide)).thenReturn(Optional.of(ratingWithDriverRating));
        when(ratingRepository.findByDriver(testDriver)).thenReturn(List.of(
                Rating.builder().driverRating(5).build()
        ));
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(modelMapper.map(testDriver, DriverDto.class)).thenReturn(new DriverDto());

        ratingService.rateDriver(testRide, 5);

        assertThat(testDriver.getRating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Rate rider should update rating and calculate new average")
    void rateRider_ShouldUpdateRatingAndCalculateAverage() {
        Rating ratingWithRiderRating = Rating.builder()
                .id(1L)
                .ride(testRide)
                .rider(testRider)
                .riderRating(null)
                .build();

        when(ratingRepository.findByRide(testRide)).thenReturn(Optional.of(ratingWithRiderRating));
        when(ratingRepository.findByRider(testRider)).thenReturn(List.of(
                Rating.builder().riderRating(4).build(),
                Rating.builder().riderRating(5).build()
        ));
        when(riderRepository.save(any(Rider.class))).thenReturn(testRider);
        when(modelMapper.map(testRider, RiderDto.class)).thenReturn(new RiderDto());

        RiderDto result = ratingService.rateRider(testRide, 5);

        assertThat(result).isNotNull();
        verify(ratingRepository).save(ratingWithRiderRating);
        assertThat(ratingWithRiderRating.getRiderRating()).isEqualTo(5);
        assertThat(testRider.getRating()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("Rate rider when already rated should throw exception")
    void rateRider_WhenAlreadyRated_ShouldThrowException() {
        Rating ratingWithRiderRating = Rating.builder()
                .id(1L)
                .riderRating(4)
                .build();

        when(ratingRepository.findByRide(testRide)).thenReturn(Optional.of(ratingWithRiderRating));

        assertThatThrownBy(() -> ratingService.rateRider(testRide, 5))
                .isInstanceOf(RuntimeConflictException.class)
                .hasMessageContaining("already been rated");
    }

    @Test
    @DisplayName("Rate rider with no existing ratings should calculate average correctly")
    void rateRider_WithNoExistingRatings_ShouldCalculateCorrectly() {
        Rating ratingWithRiderRating = Rating.builder()
                .id(1L)
                .riderRating(null)
                .build();

        when(ratingRepository.findByRide(testRide)).thenReturn(Optional.of(ratingWithRiderRating));
        when(ratingRepository.findByRider(testRider)).thenReturn(List.of(
                Rating.builder().riderRating(5).build()
        ));
        when(riderRepository.save(any(Rider.class))).thenReturn(testRider);
        when(modelMapper.map(testRider, RiderDto.class)).thenReturn(new RiderDto());

        ratingService.rateRider(testRide, 5);

        assertThat(testRider.getRating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Rate driver with non-existent rating should throw exception")
    void rateDriver_WithNonExistentRating_ShouldThrowException() {
        when(ratingRepository.findByRide(testRide)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingService.rateDriver(testRide, 5))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rating not found");
    }

    @Test
    @DisplayName("Rate rider with non-existent rating should throw exception")
    void rateRider_WithNonExistentRating_ShouldThrowException() {
        when(ratingRepository.findByRide(testRide)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingService.rateRider(testRide, 5))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rating not found");
    }

    @Test
    @DisplayName("Create new rating should save rating for ride")
    void createNewRating_ShouldSaveRatingForRide() {
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        ratingService.createNewRating(testRide);

        verify(ratingRepository).save(any(Rating.class));
    }
}