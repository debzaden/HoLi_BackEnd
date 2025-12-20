package com.example.ai_travel_agent_app.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String categoryName;
    
    @Column(columnDefinition = "NVARCHAR(500)")
    private String categoryDescription;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Service> services;
}