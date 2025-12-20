package com.example.ai_travel_agent_app.dto.booking;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequestDTO {

    private String location;
    private String specialRequest;
    private float totalPrice;
    private Long serviceId;
    private Long customerId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
