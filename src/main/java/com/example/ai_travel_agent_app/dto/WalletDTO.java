package com.example.ai_travel_agent_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private double balance;
    private LocalDateTime lastUpdate;
}