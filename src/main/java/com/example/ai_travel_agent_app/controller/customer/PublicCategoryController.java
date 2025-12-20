package com.example.ai_travel_agent_app.controller.customer;

import com.example.ai_travel_agent_app.model.Category;
import com.example.ai_travel_agent_app.service.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/public/categories")
@CrossOrigin(origins = "*")
public class PublicCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(PublicCategoryController.class);

    @Autowired
    private CategoryService categoryService;

    // Lấy tất cả categories (public - không cần auth)
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            logger.info("Public: Fetching all categories");
            List<Category> categories = categoryService.getAllCategories();
            logger.info("Public: Found {} categories", categories.size());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Public: Error fetching categories", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy category theo ID (public - không cần auth)
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            logger.info("Public: Fetching category with id: {}", id);
            return categoryService.getCategoryById(id)
                    .map(category -> {
                        logger.info("Public: Category found: {}", category.getCategoryName());
                        return ResponseEntity.ok().body(category);
                    })
                    .orElseGet(() -> {
                        logger.warn("Public: Category not found with id: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Public: Error fetching category with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}