package com.example.ai_travel_agent_app.service.worker.impl;


import com.example.ai_travel_agent_app.dto.worker.CerRequest;
import com.example.ai_travel_agent_app.dto.worker.CerResponse;
import com.example.ai_travel_agent_app.model.Certificate;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.worker.CertificateRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.CloudinaryService;
import com.example.ai_travel_agent_app.service.worker.WorkerCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WorkerCertificateServiceImpl implements WorkerCertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public List<Certificate> getCertificateByUserID(String  userEmail) {
        User user = userRepository.findFirstByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Worker worker = workerRepository.findFirstByUser(user).orElseThrow(() -> new RuntimeException("Worker not found"));
        List<Certificate> certificates = certificateRepository.findAllByWorker(worker);
        return certificates;
    }

    @Override
    public CerResponse insertCer(String userEmail, CerRequest cerRequest) {
        User user = userRepository.findFirstByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Worker worker = workerRepository.findFirstByUser(user).orElseThrow(() -> new RuntimeException("Worker not found"));

        String cerImage = null;
        // upload image
        try {
            cerImage = cloudinaryService.uploadFile(cerRequest.getCerImage(), "/Certificate");
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }

        Certificate certificate = new Certificate();
        certificate.setWorker(worker);
        certificate.setCertificateName(cerRequest.getCerName());
        certificate.setAccepted(false);
        certificate.setStartDate(cerRequest.getStartDate());
        certificate.setEndDate(cerRequest.getEndDate());
        certificate.setCertificateImage(cerImage);
        certificate.setIssuingOrganization(cerRequest.getIssuingOrganization());
        certificate.setCreatedAt(LocalDate.now());

        Certificate cerNew = certificateRepository.save(certificate);
        return toCerResponse(cerNew);
    }

    @Override
    public Certificate updateCertificate(Long id, CerRequest request) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Certificate not found"));

        String cerImage = null;
        if (request.getCerImage() != null) {
            // upload image
            try {
                cerImage = cloudinaryService.uploadFile(request.getCerImage(), "/Certificate");
                certificate.setCertificateImage(cerImage);
            } catch (RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        certificate.setCertificateName(request.getCerName());
        certificate.setIssuingOrganization(request.getIssuingOrganization());
        certificate.setStartDate(request.getStartDate());
        certificate.setEndDate(request.getEndDate());
        certificate.setAccepted(request.isAccepted());
        certificate.setUpdatedAt(LocalDate.now());

        

        return certificateRepository.save(certificate);
    }

    @Override
    public List<CerResponse> getCerByWorker(Worker worker) {
        return certificateRepository.findAllByWorker(worker).stream()
                .map(this::toCerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCertificateById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Certificate not found"));

        certificateRepository.delete(certificate);
    }

    public CerResponse toCerResponse (Certificate certificate) {
        CerResponse cerResponse = new CerResponse();

        cerResponse.setId(certificate.getId());
        cerResponse.setIssuingOrganization(certificate.getIssuingOrganization());
        cerResponse.setCerName(certificate.getCertificateName());
        cerResponse.setStartDate(certificate.getStartDate());
        cerResponse.setEndDate(certificate.getEndDate());
        cerResponse.setAccepted(certificate.isAccepted());
        cerResponse.setCerImage( certificate.getCertificateImage());

        return cerResponse;
    }
}
