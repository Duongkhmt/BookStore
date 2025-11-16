package com.bookstore.dto.response;

import com.bookstore.entity.InventoryType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private InventoryType type;
    private Integer quantityChange;
    private String reason;
    private LocalDateTime timestamp;
}