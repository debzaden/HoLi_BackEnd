package com.example.ai_travel_agent_app.service;

import java.util.List;

import com.example.ai_travel_agent_app.dto.service.TopServiceResponseDTO;

public interface TopServiceService {
    /**
     * Lấy danh sách top services dựa theo số lượng worker
     */
    List<TopServiceResponseDTO> getTopServices(int limit);
    
    /**
     * Lấy danh sách top services dựa theo số lượng reviews và rating
     */
    List<TopServiceResponseDTO> getTopServicesByReviews(int limit);

    /**
     * Lấy tổng số lượng services đang active
     */
    Long getTotalActiveServiceCount();
}
