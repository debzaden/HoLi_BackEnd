package com.example.ai_travel_agent_app.dto.notification;

import com.example.ai_travel_agent_app.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
    private Long notificationId;
    private NotificationType type;
    private String title;
    private String content;
    private LocalDateTime time;
    private boolean isRead;
}