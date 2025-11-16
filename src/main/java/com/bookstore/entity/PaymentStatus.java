package com.bookstore.entity;

public enum PaymentStatus {
    PENDING,        // Đang chờ thanh toán
    COMPLETED,      // Đã thanh toán thành công
    FAILED,         // Thanh toán thất bại
    REFUNDED        // Đã hoàn tiền
}
