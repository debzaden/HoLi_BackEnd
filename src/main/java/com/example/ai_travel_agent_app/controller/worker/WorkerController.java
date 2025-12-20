package com.example.ai_travel_agent_app.controller.worker;


import com.example.ai_travel_agent_app.dto.worker.VerifyIdentityRequest;
import com.example.ai_travel_agent_app.dto.worker.WorkerProfileResponse;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class WorkerController {

    @Autowired
    private  WorkerService workerService;

    @GetMapping("/worker/home")
    public String home() {
        return "Hello World";
    }

    // get profile worker
    @GetMapping("/worker/profile")
    public ResponseEntity<?> getProfile() {
        String workerEmail = UserFromAuth.getUserEmail();
        WorkerProfileResponse response = workerService.getProfile(workerEmail);
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/worker/verificate-id")
    public ResponseEntity<?> verificateIdentity(@Valid @RequestBody VerifyIdentityRequest workerData, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        String workerEmail = UserFromAuth.getUserEmail();
        workerService.verificateIdentity(workerData, workerEmail);
        return ResponseEntity.ok().build();
    }
}
