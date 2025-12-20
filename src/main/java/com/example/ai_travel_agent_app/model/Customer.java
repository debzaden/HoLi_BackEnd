package com.example.ai_travel_agent_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

@Setter
@Getter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @Column(columnDefinition = "NVARCHAR(20)")
    private String phoneNumber;
    
    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;
    
    @Column(columnDefinition = "NVARCHAR(10)")
    private String gender;
    
    @Column(columnDefinition = "NVARCHAR(20)")
    private String dateOfBirth;
}