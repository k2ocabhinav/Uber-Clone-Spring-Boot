package com.github.k2ocabhinav.ubercloneapp.services;

import com.github.k2ocabhinav.ubercloneapp.dto.NotificationDto;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(User user, String title, String message, NotificationType type, Long referenceId);
    List<NotificationDto> getUserNotifications(User user);
    Page<NotificationDto> getUserNotifications(User user, Pageable pageable);
    NotificationDto markAsRead(Long notificationId);
    void markAllAsRead(User user);
    long getUnreadCount(User user);
}
