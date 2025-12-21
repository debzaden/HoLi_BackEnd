package com.example.ai_travel_agent_app.service.worker;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.worker.UpdateProfileRequest;
import com.example.ai_travel_agent_app.dto.worker.VerifyIdentityRequest;
import com.example.ai_travel_agent_app.dto.worker.WorkerProfileResponse;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.model.WorkerStatus;

@Service
public interface WorkerService {
    Worker getWorkerByUser(User user);

    void verificateIdentity(VerifyIdentityRequest workerData, String email);

    void updatePhoneNumber(String userId, String phoneNumber);

    Worker getWorkerByEmail(String userEmail);

    WorkerProfileResponse getProfile(String workerEmail);

    List<WorkerProfileResponse> getAll();

    void updateWorkerStatus(Long workerId, WorkerStatus status, String rejectionReason);

    Worker getWorkerById(Long workerId);
    
    void updateProfile(String email, UpdateProfileRequest request);
    
    // New methods for AI Agent
    List<Worker> searchWorkersByCategory(String categoryName);
    
    List<Worker> searchWorkersByLocation(String location);
    
    List<Worker> searchWorkersByServiceAndLocation(String serviceName, String location);
    
    Worker findById(Long workerId);
}
