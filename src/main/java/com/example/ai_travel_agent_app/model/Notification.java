package com.example.ai_travel_agent_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String title;
    
    @Column(columnDefinition = "NVARCHAR(500)")
    private String content;
    
    private LocalDateTime time;
    
    private boolean isRead;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @PrePersist
    protected void onCreate() {
        time = LocalDateTime.now();
    }
}
