package com.example.ai_travel_agent_app.service;

import java.util.List;

import com.example.ai_travel_agent_app.dto.worker.FeaturedWorkerResponseDTO;

public interface FeaturedWorkerService {
    /**
     * Lấy danh sách featured workers dựa theo số lượng reviews và rating
     */
    List<FeaturedWorkerResponseDTO> getFeaturedWorkersByReviews(int limit);
    
    /**
     * Lấy top worker (Pro Worker) có rating và reviews cao nhất
     */
    FeaturedWorkerResponseDTO getTopWorker();
}