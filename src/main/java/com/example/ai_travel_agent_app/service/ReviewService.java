package com.example.ai_travel_agent_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.review.ReviewCreateRequestDTO;
import com.example.ai_travel_agent_app.dto.review.ReviewResponseDTO;

@Service
public interface ReviewService {

    /**
     * Customer tạo đánh giá cho booking đã hoàn thành
     */
    ReviewResponseDTO createReview(String customerEmail, ReviewCreateRequestDTO request);

    /**
     * Lấy danh sách đánh giá của một worker
     */
    List<ReviewResponseDTO> getWorkerReviews(Long workerId);

    /**
     * Lấy danh sách đánh giá của customer
     */
    List<ReviewResponseDTO> getCustomerReviews(String customerEmail);

    /**
     * Lấy thống kê đánh giá của worker
     */
    ReviewStatsDTO getWorkerReviewStats(Long workerId);

    /**
     * Kiểm tra booking đã được đánh giá chưa
     */
    boolean isBookingReviewed(Long bookingId);

    /**
     * DTO cho thống kê đánh giá
     */
    public static class ReviewStatsDTO {
        private Double averageRating;
        private Long totalReviews;

        public ReviewStatsDTO() {
        }

        public ReviewStatsDTO(Double averageRating, Long totalReviews) {
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
        }

        public Double getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(Double averageRating) {
            this.averageRating = averageRating;
        }

        public Long getTotalReviews() {
            return totalReviews;
        }

        public void setTotalReviews(Long totalReviews) {
            this.totalReviews = totalReviews;
        }
    }
}
