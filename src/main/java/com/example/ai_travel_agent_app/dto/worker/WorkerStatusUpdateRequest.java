package com.example.ai_travel_agent_app.dto.worker;

import com.example.ai_travel_agent_app.model.WorkerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerStatusUpdateRequest {
    private WorkerStatus status;
    private String rejectionReason;
}