package com.example.ai_travel_agent_app.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String serviceName;

    @Column(columnDefinition = "NTEXT")
    private String serviceDescription;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String experience;
    
    private float price;

    private boolean isActive;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "service_category",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}