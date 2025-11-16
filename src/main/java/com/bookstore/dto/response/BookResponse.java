package com.bookstore.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    // THÊM: Thông tin Nhà xuất bản
    private Long publisherId;
    private String publisherName;
}