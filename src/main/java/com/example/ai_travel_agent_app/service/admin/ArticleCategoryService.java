package com.example.ai_travel_agent_app.service.admin;

import com.example.ai_travel_agent_app.model.ArticleCategory;
import java.util.List;
import java.util.Optional;

public interface ArticleCategoryService {
    List<ArticleCategory> getAllCategories();
    Optional<ArticleCategory> getCategoryById(Long id);
    ArticleCategory createCategory(ArticleCategory category);
    ArticleCategory updateCategory(Long id, ArticleCategory category);
    void deleteCategory(Long id);
}