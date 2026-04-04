package com.github.k2ocabhinav.ubercloneapp.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingDriverDto {
    private Long driverId;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Double rating;
    private String vehicleId;
    private LocalDateTime appliedAt;
}
