package com.example.ai_travel_agent_app.service.customer.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.worker.WorkerAvailabilityDTO;
import com.example.ai_travel_agent_app.model.Schedule;
import com.example.ai_travel_agent_app.model.ScheduleStatus;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.worker.ScheduleRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.customer.WorkerAvailabilityService;

@Service
public class WorkerAvailabilityServiceImpl implements WorkerAvailabilityService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public WorkerAvailabilityDTO checkWorkerAvailability(Long workerId, LocalDate date) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        List<Schedule> daySchedules = scheduleRepository.findAllByWorker(worker).stream()
                .filter(schedule -> schedule.getStartTime().toLocalDate().equals(date))
                .toList();

        List<WorkerAvailabilityDTO.TimeSlot> availableSlots = new ArrayList<>();

        // Tạo các time slots cho ngày
        generateTimeSlots(daySchedules, availableSlots);

        return WorkerAvailabilityDTO.builder()
                .workerId(workerId)
                .date(date)
                .availableSlots(availableSlots)
                .build();
    }

    @Override
    public boolean isWorkerAvailable(Long workerId, LocalDate date, String timeSlot,
            LocalTime startTime, int duration) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        LocalDateTime requestStartTime = LocalDateTime.of(date, startTime);
        LocalDateTime requestEndTime = requestStartTime.plusHours(duration);

        List<Schedule> conflictingSchedules = scheduleRepository.findAllByWorker(worker).stream()
                .filter(schedule -> {
                    LocalDateTime scheduleStart = schedule.getStartTime();
                    LocalDateTime scheduleEnd = schedule.getEndTime();

                    // Kiểm tra xung đột thời gian
                    boolean hasTimeConflict = !(requestEndTime.isBefore(scheduleStart) ||
                            requestStartTime.isAfter(scheduleEnd));

                    // Chỉ xung đột nếu schedule đã được book hoặc completed
                    boolean isOccupied = schedule.getStatus() == ScheduleStatus.BOOKED ||
                            schedule.getStatus() == ScheduleStatus.COMPLETED;

                    return hasTimeConflict && isOccupied;
                })
                .toList();

        return conflictingSchedules.isEmpty();
    }

    private void generateTimeSlots(List<Schedule> daySchedules,
            List<WorkerAvailabilityDTO.TimeSlot> availableSlots) {
        // Morning slots (7:00 - 12:00)
        addTimeSlotsForPeriod(daySchedules, availableSlots,
                LocalTime.of(7, 0), LocalTime.of(12, 0));

        // Afternoon slots (13:00 - 17:00)
        addTimeSlotsForPeriod(daySchedules, availableSlots,
                LocalTime.of(13, 0), LocalTime.of(17, 0));

        // Evening slots (17:00 - 21:00)
        addTimeSlotsForPeriod(daySchedules, availableSlots,
                LocalTime.of(17, 0), LocalTime.of(21, 0));
    }

    private void addTimeSlotsForPeriod(List<Schedule> daySchedules,
            List<WorkerAvailabilityDTO.TimeSlot> availableSlots,
            LocalTime periodStart, LocalTime periodEnd) {

        LocalTime currentTime = periodStart;

        while (currentTime.isBefore(periodEnd)) {
            LocalTime slotEnd = currentTime.plusMinutes(30); // 30 phút mỗi slot
            if (slotEnd.isAfter(periodEnd)) {
                slotEnd = periodEnd;
            }

            boolean isAvailable = isTimeSlotAvailable(daySchedules, currentTime, slotEnd);
            ScheduleStatus status = isAvailable ? ScheduleStatus.FREE : ScheduleStatus.BOOKED;

            availableSlots.add(WorkerAvailabilityDTO.TimeSlot.builder()
                    .startTime(currentTime)
                    .endTime(slotEnd)
                    .status(status)
                    .isAvailable(isAvailable)
                    .build());

            currentTime = slotEnd;
        }
    }

    private boolean isTimeSlotAvailable(List<Schedule> daySchedules,
            LocalTime slotStart, LocalTime slotEnd) {
        return daySchedules.stream().noneMatch(schedule -> {
            LocalTime scheduleStart = schedule.getStartTime().toLocalTime();
            LocalTime scheduleEnd = schedule.getEndTime().toLocalTime();

            boolean hasTimeConflict = !(slotEnd.isBefore(scheduleStart) ||
                    slotStart.isAfter(scheduleEnd));

            boolean isOccupied = schedule.getStatus() == ScheduleStatus.BOOKED ||
                    schedule.getStatus() == ScheduleStatus.COMPLETED;

            return hasTimeConflict && isOccupied;
        });
    }
}
