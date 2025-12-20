package com.example.ai_travel_agent_app.dto.service;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequestDTO {
    private String serviceName;
    private String serviceDescription;
    private boolean isActive;
    private String experience;
    private float price;
    private Long[] categories;
}
