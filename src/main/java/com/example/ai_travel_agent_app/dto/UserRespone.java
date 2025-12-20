package com.example.ai_travel_agent_app.dto;


import com.example.ai_travel_agent_app.model.Role;
import com.example.ai_travel_agent_app.model.WorkerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRespone {
    private Long id;
    private String fullName;
    private String email;
    private String avatar;
    private Role role;
    private WorkerStatus status;
}
