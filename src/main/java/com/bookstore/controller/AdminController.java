package com.bookstore.controller;

import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.dto.response.RevenueReportResponse;
import com.bookstore.entity.OrderStatus;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Tất cả endpoints trong controller này đều yêu cầu vai trò ADMIN
public class AdminController {

    private final ReportService reportService;
//    private final BookRepository bookRepository;
//    private final OrderRepository orderRepository;
//    private final UserRepository userRepository;

//    public AdminController(ReportService reportService, BookRepository bookRepository, OrderRepository orderRepository, UserRepository userRepository) {
//        this.reportService = reportService;
//        this.bookRepository = bookRepository;
//        this.orderRepository = orderRepository;
//        this.userRepository = userRepository;
//    }
    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        RevenueReportResponse report = reportService.generateRevenueReport(startDate, endDate);
        ApiResponse<RevenueReportResponse> response = new ApiResponse<>(true, "Lấy báo cáo doanh thu thành công", report);
        return ResponseEntity.ok(response);
    }

//    // Thêm vào AdminController
//    @GetMapping("/stats")
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStats() {
//        Map<String, Object> stats = new HashMap<>();
//
//        // Lấy số liệu từ các service
//        long totalBooks = bookRepository.count();
//        long totalUsers = userRepository.count();
//        long totalOrders = orderRepository.count();
//
//        // Tính doanh thu (cần implement logic tính doanh thu)
//        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.DELIVERED);
//
//        stats.put("totalBooks", totalBooks);
//        stats.put("totalUsers", totalUsers);
//        stats.put("totalOrders", totalOrders);
//        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
//
//        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
//                true, "Lấy thống kê thành công", stats);
//        return ResponseEntity.ok(response);
//    }
}
