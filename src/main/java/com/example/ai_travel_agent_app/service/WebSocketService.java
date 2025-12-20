package com.example.ai_travel_agent_app.service;

import com.example.ai_travel_agent_app.dto.notification.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToAdmin(NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/admin/notifications", notification);
    }
    
    public void sendNotificationToUser(String userEmail, NotificationDTO notification) {
        messagingTemplate.convertAndSend("/queue/user/" + userEmail + "/notifications", notification);
    }
}