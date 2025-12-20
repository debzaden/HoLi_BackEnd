package com.example.ai_travel_agent_app.dto.review;

import java.time.LocalDateTime;

public class ReviewResponseDTO {
    private Long reviewId;
    private Long bookingId;
    private Long customerId;
    private String customerName;
    private String customerAvatar;
    private Long workerId;
    private String workerName;
    private String serviceName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponseDTO() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();

        public Builder reviewId(Long reviewId) {
            reviewResponseDTO.reviewId = reviewId;
            return this;
        }

        public Builder bookingId(Long bookingId) {
            reviewResponseDTO.bookingId = bookingId;
            return this;
        }

        public Builder customerId(Long customerId) {
            reviewResponseDTO.customerId = customerId;
            return this;
        }

        public Builder customerName(String customerName) {
            reviewResponseDTO.customerName = customerName;
            return this;
        }

        public Builder customerAvatar(String customerAvatar) {
            reviewResponseDTO.customerAvatar = customerAvatar;
            return this;
        }

        public Builder workerId(Long workerId) {
            reviewResponseDTO.workerId = workerId;
            return this;
        }

        public Builder workerName(String workerName) {
            reviewResponseDTO.workerName = workerName;
            return this;
        }

        public Builder serviceName(String serviceName) {
            reviewResponseDTO.serviceName = serviceName;
            return this;
        }

        public Builder rating(Integer rating) {
            reviewResponseDTO.rating = rating;
            return this;
        }

        public Builder comment(String comment) {
            reviewResponseDTO.comment = comment;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            reviewResponseDTO.createdAt = createdAt;
            return this;
        }

        public ReviewResponseDTO build() {
            return reviewResponseDTO;
        }
    }

    // Getters and Setters
    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAvatar() {
        return customerAvatar;
    }

    public void setCustomerAvatar(String customerAvatar) {
        this.customerAvatar = customerAvatar;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
