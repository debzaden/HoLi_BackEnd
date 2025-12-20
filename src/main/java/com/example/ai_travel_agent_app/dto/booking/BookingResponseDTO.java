package com.example.ai_travel_agent_app.dto.booking;

import java.time.LocalDateTime;

import com.example.ai_travel_agent_app.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponseDTO {
    private Long bookingId;
    private String location;
    private String specialRequest;
    private float totalPrice;
    private BookingStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Service info
    private Long serviceId;
    private String serviceName;
    private float servicePrice;

    // Customer info
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // Worker info
    private Long workerId;
    private String workerName;
    private String workerPhone;
}
