package com.example.ai_travel_agent_app.controller.admin;

import com.example.ai_travel_agent_app.model.Review;
import com.example.ai_travel_agent_app.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listAll() {
        List<Review> reviews = reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Map<String, Object>> out = reviews.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("reviewId", r.getReviewId());
            m.put("rating", r.getRating());
            m.put("comment", r.getComment());
            m.put("createdAt", r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
            if (r.getCustomer() != null) {
                Map<String, Object> cust = new HashMap<>();
                cust.put("id", r.getCustomer().getId());
                if (r.getCustomer().getUser() != null) cust.put("userName", r.getCustomer().getUser().getRealUserName());
                m.put("customer", cust);
            }
            if (r.getWorker() != null) {
                Map<String, Object> worker = new HashMap<>();
                worker.put("id", r.getWorker().getId());
                if (r.getWorker().getUser() != null) worker.put("userName", r.getWorker().getUser().getRealUserName());
                m.put("worker", worker);
            }
            if (r.getBooking() != null) {
                Map<String, Object> booking = new HashMap<>();
                booking.put("bookingId", r.getBooking().getBookingId());
                m.put("booking", booking);
            }
            return m;
        }).toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return reviewRepository.findById(id)
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("reviewId", r.getReviewId());
                    m.put("rating", r.getRating());
                    m.put("comment", r.getComment());
                    m.put("createdAt", r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
                    if (r.getCustomer() != null) {
                        Map<String, Object> cust = new HashMap<>();
                        cust.put("id", r.getCustomer().getId());
                        if (r.getCustomer().getUser() != null) cust.put("userName", r.getCustomer().getUser().getRealUserName());
                        m.put("customer", cust);
                    }
                    if (r.getWorker() != null) {
                        Map<String, Object> worker = new HashMap<>();
                        worker.put("id", r.getWorker().getId());
                        if (r.getWorker().getUser() != null) worker.put("userName", r.getWorker().getUser().getRealUserName());
                        m.put("worker", worker);
                    }
                    if (r.getBooking() != null) {
                        Map<String, Object> booking = new HashMap<>();
                        booking.put("bookingId", r.getBooking().getBookingId());
                        m.put("booking", booking);
                    }
                    return ResponseEntity.ok(m);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!reviewRepository.existsById(id)) return ResponseEntity.notFound().build();
        reviewRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Frontend expects an endpoint to update review status; Review entity may not have 'status'
    // so we echo back the requested status to keep the admin UI working.



}
