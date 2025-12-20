package com.example.ai_travel_agent_app.service.worker;

import com.example.ai_travel_agent_app.dto.worker.ScheduleRequestDTO;
import com.example.ai_travel_agent_app.dto.worker.ScheduleResponseDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WorkerScheduleService {
    List<ScheduleResponseDTO> getAllByWorker(String userEmail);

    ScheduleResponseDTO insert(String userEmail, @Valid ScheduleRequestDTO dto);

    ScheduleResponseDTO update(Long id, @Valid ScheduleRequestDTO dto);

    boolean delete(Long id);
}
