package com.bookstore.dto.response;

import com.bookstore.entity.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private String transactionId;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
}