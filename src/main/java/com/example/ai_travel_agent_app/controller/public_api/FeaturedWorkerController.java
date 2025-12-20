package com.example.ai_travel_agent_app.controller.public_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.worker.FeaturedWorkerResponseDTO;
import com.example.ai_travel_agent_app.service.FeaturedWorkerService;

@RestController
@RequestMapping("/api/public/workers")
@CrossOrigin(origins = "*")
public class FeaturedWorkerController {

    @Autowired
    private FeaturedWorkerService featuredWorkerService;

    /**
     * Lấy danh sách featured workers dựa theo reviews và rating
     */
    @GetMapping("/featured")
    public ResponseEntity<List<FeaturedWorkerResponseDTO>> getFeaturedWorkers(
            @RequestParam(defaultValue = "6") int limit) {
        try {
            List<FeaturedWorkerResponseDTO> featuredWorkers = featuredWorkerService.getFeaturedWorkersByReviews(limit);
            return ResponseEntity.ok(featuredWorkers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lấy top worker (Pro Worker)
     */
    @GetMapping("/top-worker")
    public ResponseEntity<FeaturedWorkerResponseDTO> getTopWorker() {
        try {
            FeaturedWorkerResponseDTO topWorker = featuredWorkerService.getTopWorker();
            if (topWorker == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(topWorker);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}