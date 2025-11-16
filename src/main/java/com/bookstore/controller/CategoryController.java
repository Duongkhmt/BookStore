package com.bookstore.controller;

import com.bookstore.dto.request.UpdateCategoryRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.CategoryResponse;
import com.bookstore.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Public endpoint: Lấy danh sách danh mục
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.findAllCategories();
        ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(true, "Lấy danh sách danh mục thành công", categories);
        return ResponseEntity.ok(response);
    }

    // Admin endpoint: Thêm danh mục mới
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        CategoryResponse newCategory = categoryService.createCategory(name);
        ApiResponse<CategoryResponse> response = new ApiResponse<>(true, "Thêm danh mục thành công", newCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Admin endpoint: Xóa danh mục
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        ApiResponse<?> response = new ApiResponse<>(true, "Xóa danh mục ID: " + id + " thành công", null);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
        ApiResponse<CategoryResponse> response = new ApiResponse<>(
                true, "Cập nhật danh mục thành công", updatedCategory);
        return ResponseEntity.ok(response);
    }
}