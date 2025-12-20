package com.example.ai_travel_agent_app.dto.worker;


import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyIdentityRequest {

    private Long userId;
    private String fullName;
    private String phoneNumber;
    private MultipartFile avatar;
    private String cccd;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private String otherSkill;
    private String description;
    private MultipartFile frontId;
    private MultipartFile backId;
}
