package com.example.ai_travel_agent_app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PolicyDocument {
    private String id;
    private String category;
    private String title;
    private String section;
    private String content;
    private List<String> keywords;
    
    @JsonProperty("last_updated")
    private String lastUpdated;
}
