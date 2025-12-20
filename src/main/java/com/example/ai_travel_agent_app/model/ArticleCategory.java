package com.example.ai_travel_agent_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "article_categories")
public class ArticleCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100,columnDefinition="NVARCHAR(100)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
}