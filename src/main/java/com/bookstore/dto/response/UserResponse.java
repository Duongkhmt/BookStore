package com.bookstore.dto.response;

import com.bookstore.entity.UserStatus;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private UserStatus status;
}