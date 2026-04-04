package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k2ocabhinav.ubercloneapp.config.TestSecurityConfig;
import com.github.k2ocabhinav.ubercloneapp.dto.*;
import com.github.k2ocabhinav.ubercloneapp.entities.Rider;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.Role;
import com.github.k2ocabhinav.ubercloneapp.repositories.RiderRepository;
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
class RiderControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RiderRepository riderRepository;

    private Rider testRider;

    @BeforeEach
    void setUp() {
        riderRepository.deleteAll();
        userRepository.deleteAll();
        
        User user = new User();
        user.setEmail("rider@test.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("Rider");
        user.setRoles(Set.of(Role.RIDER));
        user = userRepository.save(user);
        
        Rider rider = new Rider();
        rider.setUser(user);
        rider.setRating(5.0);
        testRider = riderRepository.save(rider);
        
        UserPrincipal principal = new UserPrincipal(user.getId(), user.getEmail(), Role.RIDER.name());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("GET /riders/getMyProfile should return rider profile")
    void getMyProfile_ShouldReturnProfile() throws Exception {
        mockMvc.perform(get("/riders/getMyProfile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("rider@test.com"));
    }

    @Test
    @DisplayName("GET /riders/getMyRides should return paginated rides")
    void getMyRides_ShouldReturnRides() throws Exception {
        mockMvc.perform(get("/riders/getMyRides")
                        .param("pageOffset", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("POST /riders/rateDriver/{rideId}/{rating} should rate driver")
    void rateDriver_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/riders/rateDriver/1/5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /riders/cancelRide/{rideId} should cancel ride")
    void cancelRide_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/riders/cancelRide/1"))
                .andExpect(status().isOk());
    }
}