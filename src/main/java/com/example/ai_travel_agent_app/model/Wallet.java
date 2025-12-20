package com.example.ai_travel_agent_app.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.sql.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    private float balance;

    @OneToOne
    private Worker worker;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
