package com.bookstore.dto.request;

import com.bookstore.entity.DifficultyLevel;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateBookRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    private String author;

    @NotBlank(message = "ISBN không được để trống")
    private String isbn;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không hợp lệ")
    private Integer stockQuantity;

    @NotNull(message = "ID danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "ID nhà xuất bản không được để trống")
    private Long publisherId;

    // MỚI
    private DifficultyLevel difficultyLevel;
    private String summary;
    private Integer publicationYear;
    private Integer pageCount;
    private List<Long> tagIds;
    private List<Long> topicIds;
}