package com.example.ai_travel_agent_app.dto.worker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.ai_travel_agent_app.model.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerAvailabilityDTO {
    private Long workerId;
    private LocalDate date;
    private List<TimeSlot> availableSlots;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;
        private ScheduleStatus status;
        private boolean isAvailable;
    }
}
