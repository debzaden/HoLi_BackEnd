package com.example.ai_travel_agent_app.service.admin.impl;

import com.example.ai_travel_agent_app.dto.category.CategoryRequestDTO;
import com.example.ai_travel_agent_app.dto.category.CategoryResponseDTO;
import com.example.ai_travel_agent_app.model.Category;
import com.example.ai_travel_agent_app.repository.CategoryRepository;
import com.example.ai_travel_agent_app.service.admin.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Existing DTO methods
    @Override
    public List<CategoryResponseDTO> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(this::toDTO).orElse(null);
    }

    @Override
    public CategoryResponseDTO insertCategory(@Valid CategoryRequestDTO categoryRequestDTO) {
        Category category = new Category();
        category.setCategoryName(categoryRequestDTO.getCategoryName());
        category.setCategoryDescription(categoryRequestDTO.getCategoryDescription());

        Category savedCategory = categoryRepository.save(category);
        return toDTO(savedCategory);
    }

    @Override
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();
            category.setCategoryName(dto.getCategoryName());
            category.setCategoryDescription(dto.getCategoryDescription());

            Category updatedCategory = categoryRepository.save(category);
            return toDTO(updatedCategory);
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        try {
            categoryRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CategoryResponseDTO toDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setCategoryDescription(category.getCategoryDescription());
        return dto;
    }

    // New methods for public controller
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            Category existing = existingCategory.get();
            existing.setCategoryName(category.getCategoryName());
            existing.setCategoryDescription(category.getCategoryDescription());
            return categoryRepository.save(existing);
        }
        throw new RuntimeException("Category not found with id: " + id);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}