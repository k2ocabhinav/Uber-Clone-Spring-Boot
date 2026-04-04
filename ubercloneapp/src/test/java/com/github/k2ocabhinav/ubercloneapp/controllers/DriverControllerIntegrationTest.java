package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k2ocabhinav.ubercloneapp.config.TestSecurityConfig;
import com.github.k2ocabhinav.ubercloneapp.dto.*;
import com.github.k2ocabhinav.ubercloneapp.entities.Driver;
import com.github.k2ocabhinav.ubercloneapp.entities.RideRequest;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.RideRequestStatus;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.Role;
import com.github.k2ocabhinav.ubercloneapp.repositories.DriverRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.RideRequestRepository;
import com.github.k2ocabhinav.ubercloneapp.repositories.UserRepository;
import com.github.k2ocabhinav.ubercloneapp.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired private RideRequestRepository rideRequestRepository;

    private Driver testDriver;

    @BeforeEach
    void setUp() {
        rideRequestRepository.deleteAll();
        driverRepository.deleteAll();
        userRepository.deleteAll();
        
        User user = new User();
        user.setEmail("driver@test.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("Driver");
        user.setRoles(Set.of(Role.DRIVER));
        user = userRepository.save(user);
        
        Driver driver = new Driver();
        driver.setUser(user);
        driver.setAvailable(true);
        driver.setRating(5.0);
        testDriver = driverRepository.save(driver);
        
        UserPrincipal principal = new UserPrincipal(user.getId(), user.getEmail(), Role.DRIVER.name());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("GET /drivers/getMyProfile should return driver profile")
    void getMyProfile_ShouldReturnProfile() throws Exception {
        mockMvc.perform(get("/drivers/getMyProfile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("driver@test.com"));
    }

    @Test
    @DisplayName("GET /drivers/getMyRides should return paginated rides")
    void getMyRides_ShouldReturnRides() throws Exception {
        mockMvc.perform(get("/drivers/getMyRides")
                        .param("pageOffset", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("POST /drivers/rateRider/{rideId}/{rating} should rate rider")
    void rateRider_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/drivers/rateRider/1/5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /drivers/cancelRide/{rideId} should cancel ride")
    void cancelRide_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/drivers/cancelRide/1"))
                .andExpect(status().isOk());
    }
}