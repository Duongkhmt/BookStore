package com.bookstore.service;

import com.bookstore.dto.request.UpdateCategoryRequest;
import com.bookstore.dto.response.CategoryResponse;
import com.bookstore.entity.Category;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Mapping: Entity to Response DTO
    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        return response;
    }

    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Tên danh mục không được để trống.");
        }

        // Kiểm tra trùng lặp
        if (categoryRepository.findAll().stream().anyMatch(c -> c.getName().equalsIgnoreCase(name))) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Tên danh mục đã tồn tại.");
        }

        Category category = new Category();
        category.setName(name.trim());
        return toResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy danh mục với ID: " + id);
        }
        // Trong ứng dụng thực tế, cần kiểm tra xem có sách nào thuộc danh mục này không
        categoryRepository.deleteById(id);
    }
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy danh mục với ID: " + id));

        String newName = request.getName().trim();

        // Kiểm tra trùng tên (trừ chính category hiện tại)
        if (!category.getName().equalsIgnoreCase(newName) &&
                categoryRepository.findAll().stream()
                        .anyMatch(c -> c.getName().equalsIgnoreCase(newName))) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                    "Tên danh mục đã tồn tại.");
        }

        category.setName(newName);
        category.setDescription(request.getDescription());
        return toResponse(categoryRepository.save(category));
    }
}