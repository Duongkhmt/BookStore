package com.bookstore.dto.request;

import com.bookstore.entity.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String password; // Optional

    @NotNull(message = "Vai trò không được để trống")
    private String role;

    // THÊM TRƯỜNG NÀY
    @NotNull(message = "Trạng thái không được để trống")
    private UserStatus status;

    @NotNull(message = "Địa chỉ không được để trống")
    private String address;

    private String phoneNumber;

}