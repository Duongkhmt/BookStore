package com.bookstore.controller;

import com.bookstore.dto.request.CreateReceiptRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.ReceiptResponse;
import com.bookstore.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@PreAuthorize("hasRole('ADMIN')")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReceiptResponse>> createReceipt(
            @Valid @RequestBody CreateReceiptRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        ReceiptResponse receipt = receiptService.createReceipt(request, username);
        ApiResponse<ReceiptResponse> response = new ApiResponse<>(
                true, "Tạo phiếu nhập kho thành công", receipt);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReceiptResponse>>> getAllReceipts() {
        List<ReceiptResponse> receipts = receiptService.getAllReceipts();
        ApiResponse<List<ReceiptResponse>> response = new ApiResponse<>(
                true, "Lấy danh sách phiếu nhập kho thành công", receipts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptById(@PathVariable Long id) {
        ReceiptResponse receipt = receiptService.getReceiptById(id);
        ApiResponse<ReceiptResponse> response = new ApiResponse<>(
                true, "Lấy chi tiết phiếu nhập kho thành công", receipt);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        ApiResponse<Void> response = new ApiResponse<>(
                true, "Xóa phiếu nhập kho thành công", null);
        return ResponseEntity.ok(response);
    }
}