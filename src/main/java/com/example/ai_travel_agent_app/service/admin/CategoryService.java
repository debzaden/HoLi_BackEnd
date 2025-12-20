package com.example.ai_travel_agent_app.service.admin;

import com.example.ai_travel_agent_app.dto.category.CategoryRequestDTO;
import com.example.ai_travel_agent_app.dto.category.CategoryResponseDTO;
import com.example.ai_travel_agent_app.model.Category;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CategoryService {
    // Existing methods
    List<CategoryResponseDTO> getAll();
    CategoryResponseDTO findById(Long id);
    CategoryResponseDTO insertCategory(@Valid CategoryRequestDTO categoryRequestDTO);
    CategoryResponseDTO update(Long id, CategoryRequestDTO dto);
    boolean delete(Long id);
    CategoryResponseDTO toDTO(Category category);

    // New methods for public controller
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}