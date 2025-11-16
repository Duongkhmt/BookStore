package com.bookstore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public  class ReceiptItemRequest {
    @NotNull(message = "ID sách không được để trống")
    private Long bookId;

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;

    @NotNull(message = "Giá nhập không được để trống")
    private java.math.BigDecimal importPrice;
}