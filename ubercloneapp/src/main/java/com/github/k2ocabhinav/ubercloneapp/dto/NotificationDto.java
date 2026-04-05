package com.github.k2ocabhinav.ubercloneapp.dto;

import com.github.k2ocabhinav.ubercloneapp.entities.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Long referenceId;
    private Boolean read;
    private LocalDateTime createdTime;
}
