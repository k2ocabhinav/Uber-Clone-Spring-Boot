package com.github.k2ocabhinav.ubercloneapp.repositories;

import com.github.k2ocabhinav.ubercloneapp.entities.Notification;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedTimeDesc(User user);

    long countByUserAndReadFalse(User user);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user AND n.read = false")
    int markAllAsReadByUser(@Param("user") User user);
}
