package com.example.ai_travel_agent_app.model;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;

@Data
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String certificateName;

    @Column(name = "certificate_image", nullable = false, columnDefinition = "NVARCHAR(500)")
    private String certificateImage;

    @Column(name = "issuing_organization", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String issuingOrganization;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "is_accepted", nullable = false)
    private boolean isAccepted;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;
}