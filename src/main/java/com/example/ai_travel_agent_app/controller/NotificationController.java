package com.example.ai_travel_agent_app.controller;

import com.example.ai_travel_agent_app.dto.notification.NotificationDTO;
import com.example.ai_travel_agent_app.service.NotificationService;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(Authentication authentication) {
        String userEmail = UserFromAuth.getUserEmail();
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userEmail);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(Authentication authentication) {
        String userEmail = UserFromAuth.getUserEmail();
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userEmail);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        String userEmail = UserFromAuth.getUserEmail();
        long count = notificationService.getUnreadCount(userEmail);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable("id") Long notificationId) {
        NotificationDTO notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        String userEmail = UserFromAuth.getUserEmail();
        notificationService.markAllAsRead(userEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeNotification(@PathVariable("id") Long notificationId) {
        notificationService.removeById(notificationId);
        return  ResponseEntity.ok("success");
    }
}