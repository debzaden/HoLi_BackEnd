package com.example.ai_travel_agent_app.repository.admin;

import com.example.ai_travel_agent_app.model.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Long> {
    Optional<ArticleCategory> findByTitle(String title);
    boolean existsByTitle(String title);
}