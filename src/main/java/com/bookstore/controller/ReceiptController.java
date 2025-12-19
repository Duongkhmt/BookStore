package com.bookstore.controller;

import com.bookstore.dto.request.CreateReceiptRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.ReceiptResponse;
import com.bookstore.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
@PreAuthorize("hasRole('ADMIN')")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    // --- SỬA: API LẤY DANH SÁCH (PHÂN TRANG) ---
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReceiptResponse>>> getAllReceipts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReceiptResponse> receipts = receiptService.getAllReceipts(page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", receipts));
    }

    // --- API LẤY CHI TIẾT (GIỮ NGUYÊN) ---
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptById(@PathVariable Long id) {
        ReceiptResponse receipt = receiptService.getReceiptById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chi tiết thành công", receipt));
    }

    // --- API TẠO MỚI ---
    @PostMapping
    public ResponseEntity<ApiResponse<ReceiptResponse>> createReceipt(
            @Valid @RequestBody CreateReceiptRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        ReceiptResponse receipt = receiptService.createReceipt(request, username);
        return new ResponseEntity<>(new ApiResponse<>(true, "Tạo phiếu nhập thành công", receipt), HttpStatus.CREATED);
    }

    // --- API XÓA ---
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Hủy phiếu nhập thành công", null));
    }
}