package com.bookstore.controller;

import com.bookstore.dto.request.CreateOrderRequest;
import com.bookstore.dto.request.UpdateOrderStatusRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.OrderResponse;
import com.bookstore.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // API: Tạo đơn hàng mới
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        OrderResponse order = orderService.createOrder(request, username);
        ApiResponse<OrderResponse> response = new ApiResponse<>(true, "Đặt hàng thành công", order);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // API: Lấy danh sách đơn hàng của user hiện tại
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(Authentication authentication) {
        String username = authentication.getName();
        List<OrderResponse> orders = orderService.getMyOrders(username);
        ApiResponse<List<OrderResponse>> response = new ApiResponse<>(true, "Lấy danh sách đơn hàng thành công", orders);
        return ResponseEntity.ok(response);
    }

    // API: Lấy chi tiết một đơn hàng theo ID
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOrderOwner(#orderId, authentication)")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(@PathVariable Long orderId) {
        OrderResponse orderDetails = orderService.getOrderDetails(orderId);
        ApiResponse<OrderResponse> response = new ApiResponse<>(true, "Lấy chi tiết đơn hàng thành công", orderDetails);
        return ResponseEntity.ok(response);
    }

    // API: Cập nhật trạng thái đơn hàng (Dành cho ADMIN)
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse order = orderService.updateOrderStatus(orderId, request);
        ApiResponse<OrderResponse> response = new ApiResponse<>(true, "Cập nhật trạng thái thành công", order);
        return ResponseEntity.ok(response);
    }

    // API: Lấy tất cả đơn hàng (chỉ ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponse> orders = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", orders));
    }
}