package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.dto.NotificationDto;
import com.github.k2ocabhinav.ubercloneapp.entities.Notification;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.NotificationType;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.repositories.NotificationRepository;
import com.github.k2ocabhinav.ubercloneapp.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public NotificationDto createNotification(User user, String title, String message,
                                           NotificationType type, Long referenceId) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return modelMapper.map(saved, NotificationDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedTimeDesc(user)
                .stream()
                .map(n -> modelMapper.map(n, NotificationDto.class))
                .toList();
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return modelMapper.map(saved, NotificationDto.class);
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }
}
