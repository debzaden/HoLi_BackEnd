package com.example.ai_travel_agent_app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.worker.FeaturedWorkerResponseDTO;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.FeaturedWorkerService;

@Service
public class FeaturedWorkerServiceImpl implements FeaturedWorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public List<FeaturedWorkerResponseDTO> getFeaturedWorkersByReviews(int limit) {
        List<Object[]> results = workerRepository.findFeaturedWorkersByReviews();

        return results.stream()
                .limit(limit)
                .map(this::mapToFeaturedWorkerResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FeaturedWorkerResponseDTO getTopWorker() {
        List<Object[]> results = workerRepository.findTopWorkerByReviews();
        
        if (results.isEmpty()) {
            return null;
        }
        
        // Lấy worker đầu tiên (có reviews và rating cao nhất)
        return mapToFeaturedWorkerResponseDTO(results.get(0));
    }

    private FeaturedWorkerResponseDTO mapToFeaturedWorkerResponseDTO(Object[] result) {
        return FeaturedWorkerResponseDTO.builder()
                .workerId(((Number) result[0]).longValue())
                .fullName((String) result[1])
                .avatar((String) result[2])
                .address((String) result[3])
                .services((String) result[4])
                .reviewCount(((Number) result[5]).longValue())
                .averageRating(result[6] != null ? ((Number) result[6]).doubleValue() : 0.0)
                .isPro((Boolean) result[7])
                .completedJobs(((Number) result[8]).longValue())
                .build();
    }
}