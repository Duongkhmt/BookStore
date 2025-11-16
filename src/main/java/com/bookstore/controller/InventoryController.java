package com.bookstore.controller;

import com.bookstore.dto.request.CreateInventoryRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.InventoryResponse;
import com.bookstore.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@PreAuthorize("hasRole('ADMIN')") // Toàn bộ module Inventory chỉ dành cho ADMIN
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // API: Ghi nhận giao dịch tồn kho thủ công (Nhập hàng, kiểm kê,...)
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventoryRecord(@RequestBody CreateInventoryRequest request) {
        InventoryResponse record = inventoryService.createInventoryRecord(request);
        ApiResponse<InventoryResponse> response = new ApiResponse<>(true, "Ghi nhận giao dịch tồn kho thành công", record);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // API: Xem lịch sử tồn kho của một cuốn sách
    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryHistoryByBook(@PathVariable Long bookId) {
        List<InventoryResponse> history = inventoryService.getInventoryHistoryByBook(bookId);
        ApiResponse<List<InventoryResponse>> response = new ApiResponse<>(true, "Lấy lịch sử tồn kho thành công", history);
        return ResponseEntity.ok(response);
    }
}