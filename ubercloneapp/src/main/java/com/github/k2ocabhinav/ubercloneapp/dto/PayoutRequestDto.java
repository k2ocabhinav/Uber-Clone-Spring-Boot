package com.github.k2ocabhinav.ubercloneapp.dto;

import com.github.k2ocabhinav.ubercloneapp.entities.enums.PayoutStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutRequestDto {
    private Long id;
    private Double amount;
    private PayoutStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String adminNote;
}
