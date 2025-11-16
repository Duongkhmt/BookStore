package com.bookstore.dto.response;

import com.bookstore.exception.ErrorResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ErrorResponse error; // Sử dụng ErrorResponse từ package exception

    // Constructor cho thành công
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = null;
    }

    // Constructor cho thất bại (Nếu không dùng GlobalExceptionHandler)
    public ApiResponse(boolean success, String message, ErrorResponse error) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.error = error;
    }
}
