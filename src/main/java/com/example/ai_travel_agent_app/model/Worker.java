package com.example.ai_travel_agent_app.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String fullName;

    private LocalDate birthDate;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String gender;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String otherSkill;

    @Column(columnDefinition = "NVARCHAR(20)")
    private String CCCD;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String frontIdImage;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String backIdImage;

    @Column(columnDefinition = "NVARCHAR(20)")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private WorkerStatus status;

    private LocalDate updateDate;

    private LocalDate phoneVerifyDate;
    private LocalDate cccdUpdateDate;
    private LocalDate activeDate;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @OneToOne
    private Wallet wallet;
    // Một người dùng có nhiều chứng chỉ
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates;

    // Một worker có nhiều service
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Service> services;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String rejectionReason;
}
