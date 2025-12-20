package com.example.ai_travel_agent_app.dto.worker;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequestDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
