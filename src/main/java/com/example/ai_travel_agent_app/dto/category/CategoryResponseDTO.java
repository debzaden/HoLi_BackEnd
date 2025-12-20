package com.example.ai_travel_agent_app.dto.category;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDTO {
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
}
