package com.example.ai_travel_agent_app.service.customer;

import java.time.LocalDate;

import com.example.ai_travel_agent_app.dto.worker.WorkerAvailabilityDTO;

public interface WorkerAvailabilityService {

    /**
     * Kiểm tra khả năng làm việc của worker trong ngày cụ thể
     */
    WorkerAvailabilityDTO checkWorkerAvailability(Long workerId, LocalDate date);

    /**
     * Kiểm tra xem worker có rảnh trong khoảng thời gian cụ thể không
     */
    boolean isWorkerAvailable(Long workerId, LocalDate date, String timeSlot,
            java.time.LocalTime startTime, int duration);
}
