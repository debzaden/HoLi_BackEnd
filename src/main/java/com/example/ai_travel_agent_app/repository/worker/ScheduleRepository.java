package com.example.ai_travel_agent_app.repository.worker;

import com.example.ai_travel_agent_app.model.Schedule;
import com.example.ai_travel_agent_app.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByScheduleId(Long scheduleId);
    boolean existsByScheduleId(Long scheduleId);

    List<Schedule> findAllByWorker(Worker worker);

    void deleteByScheduleId(Long scheduleId);
}
