package com.bookstore.dto.request;

import com.bookstore.entity.DifficultyLevel;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateBookRequest {
    private String title;
    private String author;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private Long publisherId;

    // Thông tin mở rộng (mới)
    private DifficultyLevel difficultyLevel;
    private String summary;
    private Integer publicationYear;
    private Integer pageCount;

    // Tags & Topics (mới)
    private List<Long> tagIds;    // Danh sách ID của các Tag muốn gắn
    private List<Long> topicIds;  // Danh sách ID của các Topic muốn gắn

}
