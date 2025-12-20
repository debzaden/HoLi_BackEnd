package com.example.ai_travel_agent_app.controller.customer;

import com.example.ai_travel_agent_app.model.Article;
import com.example.ai_travel_agent_app.model.ArticleCategory;
import com.example.ai_travel_agent_app.service.admin.ArticleService;
import com.example.ai_travel_agent_app.service.admin.ArticleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/public/articles")
@CrossOrigin(origins = "*")
public class CustomerArticleController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerArticleController.class);

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleCategoryService categoryService;

    // Lấy articles published (cho customer - không cần auth)
    @GetMapping("/published")
    public ResponseEntity<List<Article>> getPublishedArticles() {
        try {
            logger.info("Customer fetching published articles");
            List<Article> articles = articleService.getPublishedArticles();
            logger.info("Found {} published articles for customer", articles.size());
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching published articles for customer", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy article theo ID (cho customer - không cần auth)
    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(@PathVariable Long id) {
        try {
            logger.info("Customer fetching article with id: {}", id);
            return articleService.getArticleById(id)
                    .map(article -> {
                        // Chỉ trả về published articles cho customer
                        if (article.getStatus() == Article.ArticleStatus.PUBLISHED) {
                            logger.info("Published article found for customer: {}", article.getTitle());
                            return ResponseEntity.ok().body(article);
                        } else {
                            logger.warn("Article {} not published, access denied for customer", id);
                            return ResponseEntity.notFound().build();
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("Article not found with id: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching article with id: {} for customer", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy articles theo category (cho customer - không cần auth)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Article>> getArticlesByCategory(@PathVariable Long categoryId) {
        try {
            logger.info("Customer fetching articles by category: {}", categoryId);
            List<Article> articles = articleService.getArticlesByCategory(categoryId);
            logger.info("Found {} articles for category {} for customer", articles.size(), categoryId);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching articles by category: {} for customer", categoryId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy tất cả categories (cho customer - không cần auth)
    @GetMapping("/categories")
    public ResponseEntity<List<ArticleCategory>> getAllCategories() {
        try {
            logger.info("Customer fetching all article categories");
            List<ArticleCategory> categories = categoryService.getAllCategories();
            logger.info("Found {} categories for customer", categories.size());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error fetching categories for customer", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}