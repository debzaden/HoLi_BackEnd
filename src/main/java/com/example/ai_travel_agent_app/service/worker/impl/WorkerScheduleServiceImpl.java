package com.example.ai_travel_agent_app.service.worker.impl;

import com.example.ai_travel_agent_app.dto.worker.ScheduleRequestDTO;
import com.example.ai_travel_agent_app.dto.worker.ScheduleResponseDTO;
import com.example.ai_travel_agent_app.model.Schedule;
import com.example.ai_travel_agent_app.model.ScheduleStatus;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.worker.ScheduleRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.UserService;
import com.example.ai_travel_agent_app.service.worker.WorkerScheduleService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkerScheduleServiceImpl implements WorkerScheduleService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private ScheduleRepository scheduleRepository;


    @Transactional
    @Override
    public List<ScheduleResponseDTO> getAllByWorker(String userEmail) {
        Worker worker = workerService.getWorkerByEmail(userEmail);

        return scheduleRepository.findAllByWorker(worker).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ScheduleResponseDTO insert(String userEmail, ScheduleRequestDTO dto) {
        Worker worker = workerService.getWorkerByEmail(userEmail);

        Schedule schedule = new Schedule();
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setWorker(worker);
        schedule.setStatus(ScheduleStatus.FREE);
        Schedule newSchedule = scheduleRepository.save(schedule);

        return toDTO(newSchedule);
    }

    @Transactional
    @Override
    public ScheduleResponseDTO update(Long id, ScheduleRequestDTO dto) {
        Schedule schedule = scheduleRepository.findByScheduleId(id).get();
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());

        Schedule updateSchedule =  scheduleRepository.save(schedule);

        return toDTO(updateSchedule);
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        if (scheduleRepository.existsByScheduleId(id)) {
            scheduleRepository.deleteByScheduleId(id);
            return true;
        }

        return false;
    }

    public ScheduleResponseDTO toDTO(Schedule schedule) {
        ScheduleResponseDTO dto = new ScheduleResponseDTO();

        dto.setScheduleId(schedule.getScheduleId());
        dto.setStatus(schedule.getStatus());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());

        return dto;
    }
}
