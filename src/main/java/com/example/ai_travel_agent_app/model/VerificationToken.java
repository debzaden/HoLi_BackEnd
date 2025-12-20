package com.example.ai_travel_agent_app.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VerificationToken {
    private static final int EXPIRATION_TIME = 10;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verificationTokenId;

    @Column(name="token", columnDefinition = "NVARCHAR(255)")
    private String token;

    @Column(name="expiration_time")
    private Date expirationTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_VERIFY_TOKEN")
    )
    private User user;

    public VerificationToken(User user, String token) {
        this.token = token;
        this.user = user;
        this.expirationTime = callculateExpriationDate(EXPIRATION_TIME);
    }

    public VerificationToken(String token) {
        this.token = token;
        this.expirationTime = callculateExpriationDate(EXPIRATION_TIME);
    }

    public static Date callculateExpriationDate(int expirationTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expirationTime);
        return new Date(calendar.getTime().getTime());
    }
}