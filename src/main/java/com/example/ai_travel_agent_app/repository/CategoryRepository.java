package com.example.ai_travel_agent_app.repository;

import com.example.ai_travel_agent_app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


      // tự động fetch categories luôn

    Optional<Category> findByCategoryId(Long categoryId);

    List<Category> findByCategoryIdIn(List<Long> categoryIds);

    void deleteByCategoryId(Long categoryId);
}
