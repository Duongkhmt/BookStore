package com.bookstore.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String password; // Optional, chỉ cập nhật nếu có giá trị
    private String role; // "ROLE_USER" hoặc "ROLE_ADMIN"
}