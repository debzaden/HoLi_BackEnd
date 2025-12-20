package com.example.ai_travel_agent_app.service.admin.impl;

import com.example.ai_travel_agent_app.model.Article;
import com.example.ai_travel_agent_app.model.ArticleCategory;
import com.example.ai_travel_agent_app.repository.admin.ArticleRepository;
import com.example.ai_travel_agent_app.repository.admin.ArticleCategoryRepository;
import com.example.ai_travel_agent_app.service.admin.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCategoryRepository categoryRepository;

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Article updateArticle(Long id, Article articleData) {
        return articleRepository.findById(id)
                .map(article -> {
                    article.setTitle(articleData.getTitle());
                    article.setImage(articleData.getImage());
                    article.setDescription(articleData.getDescription());
                    article.setContent(articleData.getContent());
                    article.setCategoryArticle(articleData.getCategoryArticle());
                    article.setStatus(articleData.getStatus());
                    return articleRepository.save(article);
                })
                .orElseThrow(() -> new RuntimeException("Article not found with id " + id));
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public List<Article> getPublishedArticles() {
        return articleRepository.findPublishedArticles();
    }

    @Override
    public List<Article> getArticlesByCategory(Long categoryId) {
        ArticleCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return articleRepository.findByCategoryArticleAndStatus(category, Article.ArticleStatus.PUBLISHED);
    }

    @Override
    public List<Article> getArticlesByStatus(Article.ArticleStatus status) {
        return articleRepository.findByStatus(status);
    }

    @Override
    public Page<Article> searchArticles(String keyword, Pageable pageable) {
        return articleRepository.findByTitleContainingOrDescriptionContaining(keyword, pageable);
    }
}