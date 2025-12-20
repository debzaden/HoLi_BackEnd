package com.example.ai_travel_agent_app.dto;

import com.example.ai_travel_agent_app.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {
    private String id;
    private Double amount;
    private TransactionType type;
    private LocalDateTime createdAt;
    private String description;
}