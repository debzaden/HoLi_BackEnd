package com.example.ai_travel_agent_app.service.admin;

import com.example.ai_travel_agent_app.model.Article;
import com.example.ai_travel_agent_app.model.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArticleService {
    List<Article> getAllArticles();
    Optional<Article> getArticleById(Long id);
    Article createArticle(Article article);
    Article updateArticle(Long id, Article article);
    void deleteArticle(Long id);

    List<Article> getPublishedArticles();
    List<Article> getArticlesByCategory(Long categoryId);
    List<Article> getArticlesByStatus(Article.ArticleStatus status);

    Page<Article> searchArticles(String keyword, Pageable pageable);
}