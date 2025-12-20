package com.example.ai_travel_agent_app.service;

import com.example.ai_travel_agent_app.dto.notification.NotificationDTO;
import com.example.ai_travel_agent_app.model.NotificationType;
import com.example.ai_travel_agent_app.model.User;

import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(String userEmail, String title, String content, NotificationType type);
    List<NotificationDTO> getUserNotifications(String userEmail);
    List<NotificationDTO> getUnreadNotifications(String userEmail);
    NotificationDTO markAsRead(Long notificationId);
    void markAllAsRead(String userEmail);
    long getUnreadCount(String userEmail);
    void createNotificationForAdmin(String title, String content, NotificationType type);

    void removeById(Long notificationId);
}
