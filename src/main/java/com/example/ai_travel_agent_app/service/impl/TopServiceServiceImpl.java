package com.example.ai_travel_agent_app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.service.TopServiceResponseDTO;
import com.example.ai_travel_agent_app.repository.ServiceRepository;
import com.example.ai_travel_agent_app.service.TopServiceService;

@Service
public class TopServiceServiceImpl implements TopServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public List<TopServiceResponseDTO> getTopServices(int limit) {
        List<Object[]> results = serviceRepository.findTopCategoriesByWorkerCount();

        return results.stream()
                .limit(limit)
                .map(this::mapToTopServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TopServiceResponseDTO> getTopServicesByReviews(int limit) {
        List<Object[]> results = serviceRepository.findTopCategoriesByReviews();

        return results.stream()
                .limit(limit)
                .map(this::mapToTopServiceResponseDTOWithReviews)
                .collect(Collectors.toList());
    }

    private TopServiceResponseDTO mapToTopServiceResponseDTO(Object[] result) {
        return TopServiceResponseDTO.builder()
                .categoryId((Long) result[0])
                .categoryName((String) result[1])
                .categoryDescription((String) result[2])
                .workerCount((Long) result[3])
                .serviceCount((Long) result[4])
                .build();
    }

    private TopServiceResponseDTO mapToTopServiceResponseDTOWithReviews(Object[] result) {
        return TopServiceResponseDTO.builder()
                .categoryId((Long) result[0])
                .categoryName((String) result[1])
                .categoryDescription((String) result[2])
                .workerCount((Long) result[3])
                .serviceCount((Long) result[4])
                .reviewCount((Long) result[5])
                .averageRating((Double) result[6])
                .build();
    }

    @Override
    public Long getTotalActiveServiceCount() {
        return serviceRepository.countActiveServices();
    }
}
