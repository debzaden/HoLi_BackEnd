package com.example.ai_travel_agent_app.dto.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedWorkerResponseDTO {
    private Long workerId;
    private String fullName;
    private String avatar;
    private String address;
    private String services; // Concatenated service names
    private Long reviewCount;
    private Double averageRating;
    private Boolean isPro;
    private Long completedJobs;
}