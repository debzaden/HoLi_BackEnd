package com.example.ai_travel_agent_app.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReviewCreateRequestDTO {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    private String comment;

    public ReviewCreateRequestDTO() {
    }

    public ReviewCreateRequestDTO(Long bookingId, Integer rating, String comment) {
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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
}
