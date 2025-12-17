package com.bookstore.dto.response;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    // Thông tin người dùng (chỉ lấy tên)
    private Long userId;
    private String username;
    private String fullName;
}
