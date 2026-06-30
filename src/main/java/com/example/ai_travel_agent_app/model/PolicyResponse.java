package com.example.ai_travel_agent_app.model;

import lombok.Data;
import java.util.List;

@Data
public class PolicyResponse {
    private List<PolicyDocument> policies;
}
