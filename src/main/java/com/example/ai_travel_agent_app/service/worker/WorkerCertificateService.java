package com.example.ai_travel_agent_app.service.worker;


import com.example.ai_travel_agent_app.dto.worker.CerRequest;
import com.example.ai_travel_agent_app.dto.worker.CerResponse;
import com.example.ai_travel_agent_app.model.Certificate;
import com.example.ai_travel_agent_app.model.Worker;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WorkerCertificateService {
    List<Certificate> getCertificateByUserID(String userId);

    CerResponse insertCer(String userEmail, @Valid CerRequest cerRequest);

    void deleteCertificateById(Long id);

    Certificate updateCertificate(Long id, CerRequest request);

    List<CerResponse> getCerByWorker(Worker worker);
}
