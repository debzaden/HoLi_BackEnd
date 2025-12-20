package com.example.ai_travel_agent_app.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingStatusUpdateDTO {

    @NotNull(message = "Booking ID không được để trống")
    private Long bookingId;

    @NotNull(message = "Action không được để trống")
    private String action; // "CONFIRM", "REJECT"

    private String reason; // lý do từ chối (nếu có)
}
