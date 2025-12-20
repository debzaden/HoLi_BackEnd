package com.example.ai_travel_agent_app.dto.worker;


import com.example.ai_travel_agent_app.model.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponseDTO {
    private Long scheduleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScheduleStatus status;
}
