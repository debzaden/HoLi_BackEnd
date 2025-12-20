package com.example.ai_travel_agent_app.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerSearchCriteria {
    private String keyword;
    private String location;
    private String category;
    private Float minPrice;
    private Float maxPrice;
    private Integer minRating;
    private String experience;
    private String availableDate;
    private String timeSlot;
}
