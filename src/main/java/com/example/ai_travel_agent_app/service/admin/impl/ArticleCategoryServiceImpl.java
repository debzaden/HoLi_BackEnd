package com.example.ai_travel_agent_app.service.admin.impl;

import com.example.ai_travel_agent_app.model.ArticleCategory;
import com.example.ai_travel_agent_app.repository.admin.ArticleCategoryRepository;
import com.example.ai_travel_agent_app.service.admin.ArticleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryRepository categoryRepository;

    @Override
    public List<ArticleCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<ArticleCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public ArticleCategory createCategory(ArticleCategory category) {
        if (categoryRepository.existsByTitle(category.getTitle())) {
            throw new RuntimeException("Category with this title already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    public ArticleCategory updateCategory(Long id, ArticleCategory categoryData) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setTitle(categoryData.getTitle());
                    category.setDescription(categoryData.getDescription());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}