package com.example.ai_travel_agent_app.controller.admin;

import com.example.ai_travel_agent_app.dto.category.CategoryRequestDTO;
import com.example.ai_travel_agent_app.dto.category.CategoryResponseDTO;
import com.example.ai_travel_agent_app.service.admin.CategoryService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> list() {
        List<CategoryResponseDTO> list = categoryService.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        CategoryResponseDTO dto = categoryService.findById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CategoryRequestDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        CategoryResponseDTO created = categoryService.insertCategory(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        CategoryResponseDTO updated = categoryService.update(id, dto);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = categoryService.delete(id);
        if (!deleted) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }
}
