package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k2ocabhinav.ubercloneapp.config.TestSecurityConfig;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.Rating;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.Role;
import com.github.k2ocabhinav.ubercloneapp.repositories.DriverRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.RatingRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.RideRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.RiderRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
class RiderControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RiderRepository riderRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private RatingRepository ratingRepository;

    private Rider testRider;
    private Driver testDriver;
    private Ride confirmedRide;
    private Ride endedRide;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        rideRepository.deleteAll();
        driverRepository.deleteAll();
        riderRepository.deleteAll();
        userRepository.deleteAll();

        User riderUser = new User();
        riderUser.setEmail("rider@test.com");
        riderUser.setPassword("password123");
        riderUser.setFirstName("Test");
        riderUser.setLastName("Rider");
        riderUser.setRoles(Set.of(Role.RIDER));
        riderUser.setActive(true);
        riderUser = userRepository.save(riderUser);

        testRider = new Rider();
        testRider.setUser(riderUser);
        testRider.setRating(5.0);
        testRider = riderRepository.save(testRider);

        User driverUser = new User();
        driverUser.setEmail("driver@test.com");
        driverUser.setPassword("password123");
        driverUser.setFirstName("Test");
        driverUser.setLastName("Driver");
        driverUser.setRoles(Set.of(Role.DRIVER));
        driverUser.setActive(true);
        driverUser = userRepository.save(driverUser);

        testDriver = new Driver();
        testDriver.setUser(driverUser);
        testDriver.setAvailable(true);
        testDriver.setRating(4.9);
        testDriver.setVehicleId("KA02CD5678");
        testDriver = driverRepository.save(testDriver);

        confirmedRide = Ride.builder()
                .rider(testRider)
                .driver(testDriver)
                .rideStatus(com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus.CONFIRMED)
                .paymentMethod(PaymentMethod.WALLET)
                .fare(BigDecimal.valueOf(90.0))
                .otp("1234")
                .build();
        confirmedRide = rideRepository.save(confirmedRide);

        endedRide = Ride.builder()
                .rider(testRider)
                .driver(testDriver)
                .rideStatus(com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus.ENDED)
                .paymentMethod(PaymentMethod.WALLET)
                .fare(BigDecimal.valueOf(140.0))
                .otp("5678")
                .startedAt(LocalDateTime.now().minusMinutes(30))
                .endedAt(LocalDateTime.now().minusMinutes(10))
                .build();
        endedRide = rideRepository.save(endedRide);

        ratingRepository.save(Rating.builder()
                .ride(endedRide)
                .driver(testDriver)
                .rider(testRider)
                .build());

    }

    private MockHttpServletRequestBuilder withRiderAuth(MockHttpServletRequestBuilder builder) {
        return builder
                .header("X-Test-User-Id", testRider.getUser().getId())
                .header("X-Test-Email", testRider.getUser().getEmail())
                .header("X-Test-Role", Role.RIDER.name());
    }

    @Test
    @DisplayName("GET /riders/getMyProfile should return rider profile")
    void getMyProfile_ShouldReturnProfile() throws Exception {
        mockMvc.perform(withRiderAuth(get("/riders/getMyProfile")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.email").value("rider@test.com"));
    }

    @Test
    @DisplayName("GET /riders/getMyRides should return paginated rides")
    void getMyRides_ShouldReturnRides() throws Exception {
        mockMvc.perform(withRiderAuth(get("/riders/getMyRides")
                        .param("pageOffset", "0")
                        .param("pageSize", "10")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("POST /riders/rateDriver/{rideId}/{rating} should rate driver")
    void rateDriver_ShouldReturnOk() throws Exception {
        mockMvc.perform(withRiderAuth(post("/riders/rateDriver/" + endedRide.getId() + "/5")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.email").value("driver@test.com"));
    }

    @Test
    @DisplayName("POST /riders/cancelRide/{rideId} should cancel ride")
    void cancelRide_ShouldReturnOk() throws Exception {
        mockMvc.perform(withRiderAuth(post("/riders/cancelRide/" + confirmedRide.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rideStatus").value("CANCELLED"));
    }
}
