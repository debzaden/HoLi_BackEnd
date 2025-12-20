package com.example.ai_travel_agent_app.service.customer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.customer.WorkerCardDTO;
import com.example.ai_travel_agent_app.dto.customer.WorkerSearchCriteria;
import com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO;

@Service
public interface PublicWorkerService {
    Page<WorkerCardDTO> getActiveWorkers(Pageable pageable, String location, String service, String priceRange);

    Page<WorkerCardDTO> searchWorkers(WorkerSearchCriteria criteria, Pageable pageable);

    WorkerCardDTO getWorkerDetail(Long workerId);

    List<WorkerCardDTO> getFeaturedWorkers();

    List<WorkerCardDTO> getTopWorkers();

    List<ServiceResponseDTO> getWorkerServices(Long workerId);
}
