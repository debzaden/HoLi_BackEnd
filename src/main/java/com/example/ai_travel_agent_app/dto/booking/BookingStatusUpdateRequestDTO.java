package com.example.ai_travel_agent_app.dto.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookingStatusUpdateRequestDTO {

    @NotNull(message = "Action is required")
    @NotBlank(message = "Action cannot be blank")
    private String action; // "CONFIRM" or "REJECT"

    private String reason; // Optional reason for rejection

    public BookingStatusUpdateRequestDTO() {
    }

    public BookingStatusUpdateRequestDTO(String action, String reason) {
        this.action = action;
        this.reason = reason;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
