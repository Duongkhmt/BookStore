package com.bookstore.controller;

import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.AdvancedDashboardResponse; // Import DTO mới
import com.bookstore.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ReportService reportService;
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdvancedDashboardResponse>> getAdvancedDashboard() {
        // Gọi 1 hàm duy nhất, lấy toàn bộ số liệu đã tính toán
        AdvancedDashboardResponse stats = reportService.getAdvancedDashboard();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy dữ liệu dashboard thành công", stats));
    }
}