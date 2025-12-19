package com.bookstore.service;

import com.bookstore.dto.request.UpdateCategoryRequest;
import com.bookstore.dto.response.CategoryResponse;
import com.bookstore.entity.Category;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }
    // Mapping: Entity to Response DTO
    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setBookCount(bookRepository.countByCategoryId(category.getId()));
        return response;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable)
                .map(this::toResponse);
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