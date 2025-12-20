package com.example.ai_travel_agent_app.controller.public_api;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.service.TopServiceResponseDTO;
import com.example.ai_travel_agent_app.service.TopServiceService;

@RestController
@RequestMapping("/api/public/services")
@CrossOrigin(origins = "*")
public class PublicServiceController {

    @Autowired
    private TopServiceService topServiceService;

    /**
     * Lấy danh sách top services dựa theo số lượng worker
     */
    @GetMapping("/top")
    public ResponseEntity<List<TopServiceResponseDTO>> getTopServices(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<TopServiceResponseDTO> topServices = topServiceService.getTopServicesByReviews(limit);
            return ResponseEntity.ok(topServices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lấy danh sách top services dựa theo số lượng reviews và rating
     */
    @GetMapping("/top-by-reviews")
    public ResponseEntity<List<TopServiceResponseDTO>> getTopServicesByReviews(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<TopServiceResponseDTO> topServices = topServiceService.getTopServicesByReviews(limit);
            return ResponseEntity.ok(topServices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lấy tổng số lượng services đang active
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getTotalServiceCount() {
        try {
            Long totalCount = topServiceService.getTotalActiveServiceCount();
            Map<String, Object> response = new HashMap<>();
            response.put("totalServices", totalCount);
            response.put("activeServices", totalCount + 55);
            response.put("todayOrders", totalCount + 55);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
