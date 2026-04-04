package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k2ocabhinav.ubercloneapp.config.TestSecurityConfig;
import com.github.k2ocabhinav.ubercloneapp.entities.Rating;
import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
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

import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
class DriverControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private RiderRepository riderRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private RatingRepository ratingRepository;

    private Driver testDriver;
    private Rider testRider;
    private Ride confirmedRide;
    private Ride endedRide;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        rideRepository.deleteAll();
        driverRepository.deleteAll();
        riderRepository.deleteAll();
        userRepository.deleteAll();

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
        testDriver.setRating(5.0);
        testDriver.setVehicleId("KA01AB1234");
        testDriver = driverRepository.save(testDriver);

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
        testRider.setRating(4.8);
        testRider = riderRepository.save(testRider);

        confirmedRide = Ride.builder()
                .rider(testRider)
                .driver(testDriver)
                .rideStatus(com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus.CONFIRMED)
                .paymentMethod(PaymentMethod.WALLET)
                .fare(120.0)
                .otp("1234")
                .build();
        confirmedRide = rideRepository.save(confirmedRide);

        endedRide = Ride.builder()
                .rider(testRider)
                .driver(testDriver)
                .rideStatus(com.github.k2ocabhinav.ubercloneapp.entities.enums.RideStatus.ENDED)
                .paymentMethod(PaymentMethod.CASH)
                .fare(220.0)
                .otp("5678")
                .startedAt(LocalDateTime.now().minusMinutes(25))
                .endedAt(LocalDateTime.now().minusMinutes(5))
                .build();
        endedRide = rideRepository.save(endedRide);

        ratingRepository.save(Rating.builder()
                .ride(endedRide)
                .driver(testDriver)
                .rider(testRider)
                .build());

    }

    private MockHttpServletRequestBuilder withDriverAuth(MockHttpServletRequestBuilder builder) {
        return builder
                .header("X-Test-User-Id", testDriver.getUser().getId())
                .header("X-Test-Email", testDriver.getUser().getEmail())
                .header("X-Test-Role", Role.DRIVER.name());
    }

    @Test
    @DisplayName("GET /drivers/getMyProfile should return driver profile")
    void getMyProfile_ShouldReturnProfile() throws Exception {
        mockMvc.perform(withDriverAuth(get("/drivers/getMyProfile")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.email").value("driver@test.com"));
    }

    @Test
    @DisplayName("GET /drivers/getMyRides should return paginated rides")
    void getMyRides_ShouldReturnRides() throws Exception {
        mockMvc.perform(withDriverAuth(get("/drivers/getMyRides")
                        .param("pageOffset", "0")
                        .param("pageSize", "10")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("POST /drivers/rateRider/{rideId}/{rating} should rate rider")
    void rateRider_ShouldReturnOk() throws Exception {
        mockMvc.perform(withDriverAuth(post("/drivers/rateRider/" + endedRide.getId() + "/5")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.email").value("rider@test.com"));
    }

    @Test
    @DisplayName("POST /drivers/cancelRide/{rideId} should cancel ride")
    void cancelRide_ShouldReturnOk() throws Exception {
        mockMvc.perform(withDriverAuth(post("/drivers/cancelRide/" + confirmedRide.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rideStatus").value("CANCELLED"));
    }
}
