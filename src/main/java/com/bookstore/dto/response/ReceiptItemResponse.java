package com.bookstore.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceiptItemResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal importPrice;
    private BigDecimal subtotal;
}
