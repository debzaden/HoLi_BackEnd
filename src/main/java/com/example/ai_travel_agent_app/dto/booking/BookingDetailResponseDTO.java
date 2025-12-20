package com.example.ai_travel_agent_app.dto.booking;

import com.example.ai_travel_agent_app.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetailResponseDTO {
    private Long bookingId;
    private String location;
    private String specialRequest;
    private float totalPrice;
    private float originalPrice;
    private float discountAmount;
    private BookingStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;

    // Worker info
    private Long workerId;
    private String workerName;
    private String workerPhone;
    private String workerAvatar;

    // Customer info
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // Service info
    private Long serviceId;
    private String serviceName;
    private String serviceDescription;
    private float servicePrice;

    // Discount info
    private String voucherCode;
    private String voucherDescription;
}
