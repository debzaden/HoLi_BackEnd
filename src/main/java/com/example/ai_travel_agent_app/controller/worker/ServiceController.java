package com.example.ai_travel_agent_app.controller.worker;


import com.example.ai_travel_agent_app.dto.service.ServiceRequestDTO;
import com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO;
import com.example.ai_travel_agent_app.service.ServiceService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/services")
    public ResponseEntity<?> getServices() {
        List<ServiceResponseDTO> list = serviceService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<?> getService(@PathVariable Long id) {
        ServiceResponseDTO serviceResponseDTO = serviceService.findByServiceId(id);
        return ResponseEntity.ok(serviceResponseDTO);
    }

    @GetMapping("/worker/services")
    public ResponseEntity<?> getServiceWorker() {
        String userEmail = UserFromAuth.getUserEmail();
        List<ServiceResponseDTO> list = serviceService.findAllByWorker(userEmail);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/worker/services")
    public ResponseEntity<?> addService( @ModelAttribute ServiceRequestDTO dto) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
//            return ResponseEntity.badRequest().body(errors);
//        }
        String userEmail = UserFromAuth.getUserEmail();
        ServiceResponseDTO newService = serviceService.insert(userEmail, dto);
        return ResponseEntity.ok(newService);
    }

    @PutMapping("/worker/services/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @ModelAttribute ServiceRequestDTO dto) {
        ServiceResponseDTO updateService = serviceService.update(id, dto);
        return ResponseEntity.ok(updateService);
    }

    @DeleteMapping("/worker/services/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        boolean isDeleted = serviceService.delete(id);
        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
