package com.example.ai_travel_agent_app.controller.customer;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.worker.WorkerAvailabilityDTO;
import com.example.ai_travel_agent_app.service.customer.WorkerAvailabilityService;

@RestController
@RequestMapping("/customer")
public class WorkerAvailabilityController {

    @Autowired
    private WorkerAvailabilityService availabilityService;

    @GetMapping("/workers/{workerId}/availability")
    public ResponseEntity<?> checkWorkerAvailability(
            @PathVariable Long workerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            WorkerAvailabilityDTO availability = availabilityService.checkWorkerAvailability(workerId, date);
            return ResponseEntity.ok(availability);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
