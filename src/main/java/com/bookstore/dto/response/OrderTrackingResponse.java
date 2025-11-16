package com.bookstore.dto.response;

import com.bookstore.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderTrackingResponse {
    private Long id;
    private Long orderId;
    private OrderStatus status; // Sử dụng Enum trực tiếp
    private LocalDateTime timestamp;
    private String note;
}