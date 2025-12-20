package com.example.ai_travel_agent_app.controller.admin;

import com.example.ai_travel_agent_app.model.ArticleCategory;
import com.example.ai_travel_agent_app.service.admin.ArticleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/admin/articleCategories")
@CrossOrigin(origins = "*")
public class ArticleCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleCategoryController.class);

    @Autowired
    private ArticleCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<ArticleCategory>> getAllCategories() {
        try {
            logger.info("Fetching all article categories");
            List<ArticleCategory> categories = categoryService.getAllCategories();
            logger.info("Found {} categories", categories.size());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error fetching categories", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleCategory> getCategoryById(@PathVariable Long id) {
        try {
            logger.info("Fetching category with id: {}", id);
            return categoryService.getCategoryById(id)
                    .map(category -> {
                        logger.info("Category found: {}", category.getTitle());
                        return ResponseEntity.ok().body(category);
                    })
                    .orElseGet(() -> {
                        logger.warn("Category not found with id: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching category with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ArticleCategory> createCategory(@RequestBody ArticleCategory category) {
        try {
            logger.info("Creating new category: {}", category.getTitle());
            ArticleCategory newCategory = categoryService.createCategory(category);
            logger.info("Category created with id: {}", newCategory.getId());
            return ResponseEntity.ok(newCategory);
        } catch (RuntimeException e) {
            logger.warn("Error creating category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating category", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleCategory> updateCategory(@PathVariable Long id, @RequestBody ArticleCategory category) {
        try {
            logger.info("Updating category with id: {}", id);
            ArticleCategory updatedCategory = categoryService.updateCategory(id, category);
            logger.info("Category updated successfully");
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            logger.warn("Category not found for update: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating category with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            logger.info("Deleting category with id: {}", id);
            categoryService.deleteCategory(id);
            logger.info("Category deleted successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting category with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}