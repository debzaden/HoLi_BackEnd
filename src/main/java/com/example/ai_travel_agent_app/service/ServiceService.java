package com.example.ai_travel_agent_app.service;

import com.example.ai_travel_agent_app.dto.category.CategoryResponseDTO;
import com.example.ai_travel_agent_app.dto.service.ServiceRequestDTO;
import com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO;
import com.example.ai_travel_agent_app.model.Category;
import jakarta.validation.Valid;

import java.util.List;

public interface ServiceService {
    ServiceResponseDTO insert(String userEmail, ServiceRequestDTO dto);

    List<ServiceResponseDTO> findAll();

    ServiceResponseDTO findByServiceId(Long id);

    ServiceResponseDTO update(Long id, ServiceRequestDTO dto);

    boolean delete(Long id);

    List<ServiceResponseDTO> findAllByWorker(String userEmail);

    void updateStatusService(Long serviceId);
}
