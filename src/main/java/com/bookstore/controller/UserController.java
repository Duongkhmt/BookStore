package com.bookstore.controller;

import com.bookstore.dto.request.CreateUserRequest;
import com.bookstore.dto.request.UpdateUserRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.UserResponse;
import com.bookstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserResponse> userPage = userService.findAllUsers(page, size);

        // ApiResponse bây giờ chứa Page<UserResponse> thay vì List
        ApiResponse<Page<UserResponse>> response = new ApiResponse<>(
                true, "Lấy danh sách người dùng thành công", userPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.findUserById(id);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                true, "Lấy thông tin người dùng thành công", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse newUser = userService.createUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                true, "Tạo người dùng thành công", newUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                true, "Cập nhật người dùng thành công", updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ApiResponse<?> response = new ApiResponse<>(
                true, "Xóa người dùng thành công", null);
        return ResponseEntity.ok(response);
    }
}