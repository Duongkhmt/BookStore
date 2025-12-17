package com.bookstore.dto.response;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookSimpleResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String difficultyLevel;
}