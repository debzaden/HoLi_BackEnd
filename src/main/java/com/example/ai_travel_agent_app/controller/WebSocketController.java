package com.example.ai_travel_agent_app.controller;

import com.example.ai_travel_agent_app.dto.notification.NotificationDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/notifications")
    @SendTo("/topic/admin/notifications")
    public NotificationDTO broadcastNotification(NotificationDTO notification) {
        return notification;
    }
}