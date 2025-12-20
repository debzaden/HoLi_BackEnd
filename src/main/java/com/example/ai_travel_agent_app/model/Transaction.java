package com.example.ai_travel_agent_app.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    private String id;

    private Double amount; // số tiền nạp hoặc trừ

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String description;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
