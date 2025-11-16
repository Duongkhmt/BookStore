package com.bookstore.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT_DATA(HttpStatus.BAD_REQUEST, "40001", "Dữ liệu đầu vào không hợp lệ."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "40002", "Không đủ số lượng tồn kho."),
    ISBN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "40003", "ISBN sách đã tồn tại."),

    // 401 Unauthorized / 403 Forbidden
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "40101", "Bạn cần đăng nhập để truy cập."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "40301", "Bạn không có quyền truy cập tài nguyên này."),
    // THÊM: Lỗi trạng thái tài khoản
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "40102", "Tài khoản đã bị khóa."),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "40103", "Tài khoản không hoạt động."),

    // 404 Not Found
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "40401", "Tài nguyên không được tìm thấy."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "40402", "Người dùng không tồn tại."),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "40403", "Sách không tồn tại."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "40404", "Không tìm thấy đơn hàng với ID."),

    // 409 Conflict
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "40901", "Tên người dùng hoặc email đã tồn tại."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50001", "Lỗi máy chủ nội bộ không xác định.");



    private final HttpStatus httpStatus; // Mã HTTP (400, 404, 500...)
    private final String code;          // Mã lỗi tùy chỉnh (ví dụ: "40001")
    private final String message;       // Thông báo lỗi mặc định
}
