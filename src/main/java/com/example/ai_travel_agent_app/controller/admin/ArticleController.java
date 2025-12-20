package com.example.ai_travel_agent_app.controller.admin;

import com.example.ai_travel_agent_app.model.Article;
import com.example.ai_travel_agent_app.service.admin.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    // Lấy tất cả articles
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            logger.info("Fetching all articles");
            List<Article> articles = articleService.getAllArticles();
            logger.info("Found {} articles", articles.size());
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching all articles", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy articles published (cho customer)
    @GetMapping("/published")
    public ResponseEntity<List<Article>> getPublishedArticles() {
        try {
            logger.info("Fetching published articles");
            List<Article> articles = articleService.getPublishedArticles();
            logger.info("Found {} published articles", articles.size());
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching published articles", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy article theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        try {
            logger.info("Fetching article with id: {}", id);
            return articleService.getArticleById(id)
                    .map(article -> {
                        logger.info("Article found: {}", article.getTitle());
                        return ResponseEntity.ok().body(article);
                    })
                    .orElseGet(() -> {
                        logger.warn("Article not found with id: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching article with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Tạo article mới
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        try {
            logger.info("Creating new article: {}", article.getTitle());
            Article savedArticle = articleService.createArticle(article);
            logger.info("Article created with id: {}", savedArticle.getId());
            return ResponseEntity.ok(savedArticle);
        } catch (Exception e) {
            logger.error("Error creating article", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cập nhật article
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        try {
            logger.info("Updating article with id: {}", id);
            Article updatedArticle = articleService.updateArticle(id, article);
            logger.info("Article updated successfully");
            return ResponseEntity.ok(updatedArticle);
        } catch (RuntimeException e) {
            logger.warn("Article not found for update: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating article with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Xóa article
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        try {
            logger.info("Deleting article with id: {}", id);
            articleService.deleteArticle(id);
            logger.info("Article deleted successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting article with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy articles theo category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Article>> getArticlesByCategory(@PathVariable Long categoryId) {
        try {
            logger.info("Fetching articles by category: {}", categoryId);
            List<Article> articles = articleService.getArticlesByCategory(categoryId);
            logger.info("Found {} articles for category {}", articles.size(), categoryId);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching articles by category: {}", categoryId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy articles theo status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Article>> getArticlesByStatus(@PathVariable Article.ArticleStatus status) {
        try {
            logger.info("Fetching articles by status: {}", status);
            List<Article> articles = articleService.getArticlesByStatus(status);
            logger.info("Found {} articles with status {}", articles.size(), status);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching articles by status: {}", status, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Tìm kiếm articles
    @GetMapping("/search")
    public ResponseEntity<Page<Article>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Searching articles with keyword: {}, page: {}, size: {}", keyword, page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<Article> articles = articleService.searchArticles(keyword, pageable);
            logger.info("Found {} articles for search term: {}", articles.getTotalElements(), keyword);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error searching articles with keyword: {}", keyword, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}