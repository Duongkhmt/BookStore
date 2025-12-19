package com.bookstore.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal priceAtOrder; // Giá tại thời điểm đặt hàng
    // Thêm cái này để hiển thị cột "Thành tiền"
    private BigDecimal subtotal;
}