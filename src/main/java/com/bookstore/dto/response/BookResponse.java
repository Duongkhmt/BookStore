package com.bookstore.dto.response;


import com.bookstore.entity.DifficultyLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private Integer stockQuantity;

    // Thông tin phân loại
    private Long categoryId;
    private String categoryName;
    private Long publisherId;
    private String publisherName;

    // MỚI: Thông tin mở rộng
    private DifficultyLevel difficultyLevel;
    private String summary;
    private Integer publicationYear;
    private Integer pageCount;

    // Tags và Topics
    private List<TagResponse> tags;
    private List<TopicResponse> topics;

    // Thống kê
    private Double averageRating;
    private Integer totalReviews;

    // Sách liên quan
    private List<BookSimpleResponse> relatedBooks;
}
