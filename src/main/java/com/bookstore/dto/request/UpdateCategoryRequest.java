package com.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCategoryRequest {
    @NotBlank(message = "Tên thể loại không được để trống")
    private String name;
    private String description;
}