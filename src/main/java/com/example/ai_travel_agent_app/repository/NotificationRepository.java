package com.example.ai_travel_agent_app.repository;

import com.example.ai_travel_agent_app.model.Notification;
import com.example.ai_travel_agent_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByTimeDesc(User user);
    List<Notification> findByUserAndIsReadOrderByTimeDesc(User user, boolean isRead);
    long countByUserAndIsRead(User user, boolean isRead);

    void deleteByNotificationId(Long notificationId);
}