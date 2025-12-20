package com.example.ai_travel_agent_app.controller.customer;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.review.ReviewCreateRequestDTO;
import com.example.ai_travel_agent_app.dto.review.ReviewResponseDTO;
import com.example.ai_travel_agent_app.service.ReviewService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreateRequestDTO request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            String customerEmail = UserFromAuth.getUserEmail();
            ReviewResponseDTO review = reviewService.createReview(customerEmail, request);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> getMyReviews() {
        try {
            String customerEmail = UserFromAuth.getUserEmail();
            List<ReviewResponseDTO> reviews = reviewService.getCustomerReviews(customerEmail);
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bookings/{bookingId}/review-status")
    public ResponseEntity<?> checkReviewStatus(@PathVariable Long bookingId) {
        try {
            boolean isReviewed = reviewService.isBookingReviewed(bookingId);
            return ResponseEntity.ok(Map.of("isReviewed", isReviewed));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
