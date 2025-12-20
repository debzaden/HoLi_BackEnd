package com.example.ai_travel_agent_app.repository.admin;

import com.example.ai_travel_agent_app.model.Article;
import com.example.ai_travel_agent_app.model.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Tìm theo status
    List<Article> findByStatus(Article.ArticleStatus status);

    // Tìm theo category
    List<Article> findByCategoryArticle(ArticleCategory category);

    // Tìm theo title chứa keyword
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword%")
    Page<Article> findByTitleContainingOrDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);

    // Lấy articles published và sắp xếp theo ngày tạo
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.createdAt DESC")
    List<Article> findPublishedArticles();

    // Lấy articles theo category và status
    List<Article> findByCategoryArticleAndStatus(ArticleCategory category, Article.ArticleStatus status);
}