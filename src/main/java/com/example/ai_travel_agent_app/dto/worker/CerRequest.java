package com.example.ai_travel_agent_app.dto.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CerRequest {

    private String cerName;
    private String issuingOrganization;
    private LocalDate startDate;
    private LocalDate endDate;
    private MultipartFile cerImage;
    private boolean isAccepted;
}
