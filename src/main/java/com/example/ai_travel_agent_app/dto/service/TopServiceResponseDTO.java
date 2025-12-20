package com.example.ai_travel_agent_app.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopServiceResponseDTO {
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private Long workerCount;
    private Long serviceCount;
    private Long reviewCount;
    private Double averageRating;
}
