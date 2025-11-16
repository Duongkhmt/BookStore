package com.bookstore.exception;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String status;      // Mã HTTP (ví dụ: "400 BAD_REQUEST")
    private final String errorCode;   // Mã lỗi tùy chỉnh (ví dụ: "40001")
    private final String message;     // Thông báo chi tiết lỗi
    private final String path;        // URI của request
}