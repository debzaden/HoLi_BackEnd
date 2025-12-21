package com.example.ai_travel_agent_app.dto.worker;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Other skill is required")
    private String otherSkill;
}
