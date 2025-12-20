package com.example.ai_travel_agent_app.dto.service;


import com.example.ai_travel_agent_app.dto.category.CategoryResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResponseDTO {
    private Long serviceId;
    private String serviceName;
    private String title; // Thêm trường title
    private String serviceDescription;
    private boolean isActive;
    private String experience;
    private float price;
    private List<CategoryResponseDTO> categories;
}
