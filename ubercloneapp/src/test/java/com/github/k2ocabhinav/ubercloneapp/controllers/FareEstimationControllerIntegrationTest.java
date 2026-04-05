package com.github.k2ocabhinav.ubercloneapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateDto;
import com.github.k2ocabhinav.ubercloneapp.dto.FareEstimateRequestDto;
import com.github.k2ocabhinav.ubercloneapp.dto.PointDto;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.PaymentMethod;
import com.github.k2ocabhinav.ubercloneapp.services.FareEstimationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FareEstimationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FareEstimationService fareEstimationService;

    private FareEstimateRequestDto requestDto;
    private FareEstimateDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = FareEstimateRequestDto.builder()
                .pickupLocation(new PointDto(new double[]{77.1, 28.1}))
                .dropOffLocation(new PointDto(new double[]{77.2, 28.2}))
                .paymentMethod(PaymentMethod.WALLET)
                .build();

        responseDto = FareEstimateDto.builder()
                .estimatedFare(BigDecimal.valueOf(150.0))
                .baseFare(BigDecimal.valueOf(150.0))
                .surgeMultiplier(1.0)
                .discountAmount(BigDecimal.ZERO)
                .distanceKm(5.0)
                .durationMinutes(15.0)
                .applicablePromos(Collections.emptyList())
                .build();
    }

    @Test
    @WithMockUser(roles = "RIDER")
    @DisplayName("POST /riders/fare-estimate should return 200 and estimate data in a wrapped response")
    void getFareEstimate_ShouldReturnOk() throws Exception {
        when(fareEstimationService.estimateFare(any(FareEstimateRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/riders/fare-estimate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estimatedFare").value(150.0))
                .andExpect(jsonPath("$.data.distanceKm").value(5.0));
    }

    @Test
    @DisplayName("POST /riders/fare-estimate without authentication should return 401/403")
    void getFareEstimate_NoAuth_ShouldReturnForbiddenOrUnauthorized() throws Exception {
        // Many Spring Security configurations return 403 Forbidden for unauthenticated access 
        // to protected endpoints when no authentication entry point is explicitly defined.
        mockMvc.perform(post("/riders/fare-estimate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }
}
