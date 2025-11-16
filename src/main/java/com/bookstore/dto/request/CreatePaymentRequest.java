package com.bookstore.dto.request;

import com.bookstore.entity.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {
    @NotNull(message = "ID đơn hàng không được để trống")
    private Long orderId;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    // Tùy chọn: mã giao dịch
    private String transactionId;

    @NotNull(message = "Trạng thái thanh toán không được để trống")
    private PaymentStatus status;
}