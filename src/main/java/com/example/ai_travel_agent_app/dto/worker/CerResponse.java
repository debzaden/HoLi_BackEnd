package com.example.ai_travel_agent_app.dto.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CerResponse {

    private Long id;
    private String cerName;
    private String issuingOrganization;
    private LocalDate startDate;
    private LocalDate endDate;
    private String cerImage;
    private boolean isAccepted;
}
