package com.bookstore.controller;

import com.bookstore.dto.request.UpdatePaymentStatusRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.PaymentResponse;
import com.bookstore.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // API: Cập nhật trạng thái thanh toán (Thường được gọi bởi Webhook hoặc ADMIN)
    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestBody UpdatePaymentStatusRequest request) {

        PaymentResponse payment = paymentService.updatePaymentStatus(paymentId, request);
        ApiResponse<PaymentResponse> response = new ApiResponse<>(true, "Cập nhật trạng thái thanh toán thành công", payment);
        return ResponseEntity.ok(response);
    }

    // API: Lấy chi tiết thanh toán (ADMIN)
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentDetails(@PathVariable Long paymentId) {
        PaymentResponse payment = paymentService.getPaymentDetails(paymentId);
        ApiResponse<PaymentResponse> response = new ApiResponse<>(true, "Lấy chi tiết thanh toán thành công", payment);
        return ResponseEntity.ok(response);
    }
}