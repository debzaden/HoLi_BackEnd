package com.example.ai_travel_agent_app.controller.worker;


import com.example.ai_travel_agent_app.dto.worker.CerRequest;
import com.example.ai_travel_agent_app.dto.worker.CerResponse;
import com.example.ai_travel_agent_app.model.Certificate;
import com.example.ai_travel_agent_app.service.worker.WorkerCertificateService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
public class WorkerCertificateController {

    @Autowired
    private WorkerCertificateService workerCertificateService;

    @GetMapping("/worker/certificate")
    public ResponseEntity<?> getCertificates() {

       String userEmail = UserFromAuth.getUserEmail();

        List<Certificate> certificates = workerCertificateService.getCertificateByUserID(userEmail);

        List<CerResponse> cerResponses = certificates.stream()
                .map(cer -> new CerResponse(
                        cer.getId(),
                        cer.getCertificateName(),
                        cer.getCertificateImage(),
                        cer.getStartDate(),
                        cer.getEndDate(),
                        cer.getIssuingOrganization(),
                        cer.isAccepted()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(cerResponses);

    }

    @PostMapping("/worker/certificate")
    public ResponseEntity<?> insertCertificate(@ModelAttribute CerRequest cerRequest, BindingResult result) {
//        if (result.hasErrors()) {
//            Map<String, String> errors = BindingValidError.getValidationErrors(result);
//            return ResponseEntity.badRequest().body(errors);
//        }
        String userEmail = UserFromAuth.getUserEmail();
        CerResponse cer = workerCertificateService.insertCer(userEmail, cerRequest);

        return ResponseEntity.ok(cer);
    }

    @DeleteMapping("/worker/certificate/{id}")
    public ResponseEntity<?> deleteCertificate(@PathVariable("id") Long id) {
        try {
            workerCertificateService.deleteCertificateById(id);
            return ResponseEntity.ok().body("Certificate deleted successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Certificate not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting.");
        }
    }

    @PutMapping("/worker/certificate/{id}")
    public ResponseEntity<?> updateCertificate(
            @PathVariable Long id,
            @RequestBody CerRequest request
    ) {
        try {
            Certificate updatedCertificate = workerCertificateService.updateCertificate(id, request);
            return ResponseEntity.ok().body("Certificate updated successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Certificate not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed.");
        }
    }
}
