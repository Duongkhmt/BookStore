package com.bookstore.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
//    private Set<OrderItemResponse> items;
//    private List<OrderTrackingResponse> trackingHistory;
private List<OrderItemResponse> items = new ArrayList<>();
    private List<OrderTrackingResponse> trackingHistory = new ArrayList<>();
    // THÊM: Chi tiết thanh toán (Dùng DTO đã tạo)
    private PaymentResponse payment;
    private String username;
}