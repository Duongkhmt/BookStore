package com.bookstore.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateBookRequest {
    private String title;
    private String author;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private Long publisherId;
}
