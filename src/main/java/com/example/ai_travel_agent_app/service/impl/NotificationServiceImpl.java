package com.example.ai_travel_agent_app.service.impl;

import com.example.ai_travel_agent_app.dto.notification.NotificationDTO;
import com.example.ai_travel_agent_app.model.Notification;
import com.example.ai_travel_agent_app.model.NotificationType;
import com.example.ai_travel_agent_app.model.Role;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.repository.NotificationRepository;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.service.NotificationService;
import com.example.ai_travel_agent_app.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WebSocketService webSocketService;
    
    @Override
    @Transactional
    public NotificationDTO createNotification(String userEmail, String title, String content, NotificationType type) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRead(false);
        notification.setTime(LocalDateTime.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        NotificationDTO notificationDTO = mapToDTO(savedNotification);
        
        // Send real-time notification to the user
        webSocketService.sendNotificationToUser(userEmail, notificationDTO);
        
        return notificationDTO;
    }
    
    @Override
    public List<NotificationDTO> getUserNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> notifications = notificationRepository.findByUserOrderByTimeDesc(user);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<NotificationDTO> getUnreadNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> notifications = notificationRepository.findByUserAndIsReadOrderByTimeDesc(user, false);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return mapToDTO(updatedNotification);
    }
    
    @Override
    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadOrderByTimeDesc(user, false);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    @Override
    public long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Override
    @Transactional
    public void createNotificationForAdmin(String title, String content, NotificationType type) {
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        for (User admin : admins) {
            NotificationDTO notification = createNotification(admin.getEmail(), title, content, type);
            // Send real-time notification to admin
            webSocketService.sendNotificationToAdmin(notification);
        }
    }

    @Override
    @Transactional
    public void removeById(Long notificationId) {
        notificationRepository.deleteByNotificationId(notificationId);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .time(notification.getTime())
                .isRead(notification.isRead())
                .build();
    }
}
