package com.example.ai_travel_agent_app.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteDTO {
    private Long favoriteId;
    private Long customerId;
    private Long workerId;
    private WorkerCardDTO worker;
    private LocalDateTime createdAt;
}
