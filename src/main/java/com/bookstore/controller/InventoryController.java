package com.bookstore.controller;

import com.bookstore.dto.request.CreateInventoryRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.InventoryResponse;
import com.bookstore.dto.response.InventoryStatsResponse;
import com.bookstore.entity.InventoryType;
import com.bookstore.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@PreAuthorize("hasRole('ADMIN')")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // --- 1. API: Lấy thống kê nhanh (Dashboard) ---
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<InventoryStatsResponse>> getStats() {
        InventoryStatsResponse stats = inventoryService.getInventoryStats();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê thành công", stats));
    }

    // --- 2. API: Lấy lịch sử giao dịch (ĐÃ SỬA: PHÂN TRANG) ---
    // Lưu ý: Chỉ giữ hàm này, XÓA hàm getAllInventoryRecords trả về List cũ đi
    @GetMapping
    public ResponseEntity<ApiResponse<Page<InventoryResponse>>> getAllInventoryRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<InventoryResponse> records = inventoryService.getAllInventoryRecords(page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", records));
    }

    // --- 3. API: Ghi nhận giao dịch thủ công ---
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventoryRecord(@RequestBody CreateInventoryRequest request) {
        InventoryResponse record = inventoryService.createInventoryRecord(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Ghi nhận thành công", record), HttpStatus.CREATED);
    }

    // --- 4. API: Xem lịch sử của 1 cuốn sách ---
    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryHistoryByBook(@PathVariable Long bookId) {
        List<InventoryResponse> history = inventoryService.getInventoryHistoryByBook(bookId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy lịch sử tồn kho thành công", history));
    }

    // --- 5. API: Lấy chi tiết 1 record ---
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryRecordById(@PathVariable Long id) {
        InventoryResponse record = inventoryService.getInventoryRecordById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chi tiết thành công", record));
    }

    // --- 6. API: Lấy theo loại (IN/OUT) ---
    // (Có thể giữ lại nếu cần dùng, nhưng Frontend hiện tại đang dùng API số 2 là đủ)
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryRecordsByType(@PathVariable InventoryType type) {
        List<InventoryResponse> records = inventoryService.getInventoryRecordsByType(type);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy theo loại thành công", records));
    }
}