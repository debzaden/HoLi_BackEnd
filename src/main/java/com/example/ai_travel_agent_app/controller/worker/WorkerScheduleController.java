package com.example.ai_travel_agent_app.controller.worker;


import com.example.ai_travel_agent_app.dto.worker.ScheduleRequestDTO;
import com.example.ai_travel_agent_app.dto.worker.ScheduleResponseDTO;
import com.example.ai_travel_agent_app.service.worker.WorkerScheduleService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WorkerScheduleController {

    @Autowired
    private WorkerScheduleService workerScheduleService;

    @GetMapping("/worker/schedules")
    public ResponseEntity<?> getAllSchedules() {
        String userEmail = UserFromAuth.getUserEmail();
        List<ScheduleResponseDTO> list = workerScheduleService.getAllByWorker(userEmail);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/worker/schedules")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleRequestDTO dto) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//        }

        String userEmail = UserFromAuth.getUserEmail();

        ScheduleResponseDTO newSchedule = workerScheduleService.insert(userEmail, dto);

        return ResponseEntity.ok(newSchedule);
    }

    @PutMapping("/worker/schedules/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDTO dto) {
        ScheduleResponseDTO responseDTO = workerScheduleService.update(id, dto);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/worker/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        boolean isDeleted = workerScheduleService.delete(id);

        if (!isDeleted) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
