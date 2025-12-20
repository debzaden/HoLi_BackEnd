package com.example.ai_travel_agent_app.controller.customer;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.review.ReviewResponseDTO;
import com.example.ai_travel_agent_app.service.ReviewService;

@RestController
@RequestMapping("/public")
public class PublicReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/workers/{workerId}/reviews")
    public ResponseEntity<?> getWorkerReviews(@PathVariable Long workerId) {
        try {
            List<ReviewResponseDTO> reviews = reviewService.getWorkerReviews(workerId);
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/workers/{workerId}/review-stats")
    public ResponseEntity<?> getWorkerReviewStats(@PathVariable Long workerId) {
        try {
            ReviewService.ReviewStatsDTO stats = reviewService.getWorkerReviewStats(workerId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
