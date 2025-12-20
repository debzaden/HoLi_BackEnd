package com.example.ai_travel_agent_app.controller.customer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.model.WorkerStatus;
import com.example.ai_travel_agent_app.repository.ServiceRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicStatsController {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        Map<String, Object> stats = new HashMap<>();

        // Đếm số worker active
        long activeWorkerCount = workerRepository.countByStatus(WorkerStatus.ACTIVE);
        stats.put("totalWorkers", activeWorkerCount);

        // Đếm số service active
        long activeServiceCount = serviceRepository.countByIsActiveTrue();
        stats.put("totalServices", activeServiceCount);

        // Đánh giá trung bình (giả lập)
        stats.put("averageRating", 4.8);
        stats.put("totalReviews", 2340);

        // Số tỉnh thành phủ sóng
        stats.put("provincesCovered", 63);

        return ResponseEntity.ok(stats);
    }
}
