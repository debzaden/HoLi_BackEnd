package com.example.ai_travel_agent_app.dto.worker;


import com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO;
import com.example.ai_travel_agent_app.model.WorkerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerProfileResponse {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String description;
    private String otherSkill;
    private String address;
    private String phone;
    private String email;
    private String avatar;
    private String cccd;
    private String cccdFrontImage;
    private String cccdBackImage;
    private WorkerStatus status;
    private Double rating;
    private int bookingCount;
    private  LocalDate registerDate;
    private List<CerResponse> certificates;
    private List<ServiceResponseDTO> services;
    private LocalDate phoneVerifyDate;
    private LocalDate cccdUpdateDate;
    private LocalDate activeDate;


}
