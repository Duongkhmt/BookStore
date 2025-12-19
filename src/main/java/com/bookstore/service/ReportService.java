package com.bookstore.service;

import com.bookstore.dto.response.*;
import com.bookstore.entity.OrderStatus;
import com.bookstore.repository.*;
import com.bookstore.service.helper.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public AdvancedDashboardResponse getAdvancedDashboard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfYesterday = startOfToday.minusDays(1);
        LocalDateTime endOfYesterday = startOfToday.minusSeconds(1);

        // 1. KPI Calculation
        BigDecimal revToday = orderRepo.sumRevenueBetween(startOfToday, now);
        BigDecimal revYesterday = orderRepo.sumRevenueBetween(startOfYesterday, endOfYesterday);
        double revGrowth = calculateGrowth(revToday, revYesterday);

        long ordersToday = orderRepo.countOrdersBetween(startOfToday, now);
        long ordersYesterday = orderRepo.countOrdersBetween(startOfYesterday, endOfYesterday);
        double orderGrowth = calculateGrowth(BigDecimal.valueOf(ordersToday), BigDecimal.valueOf(ordersYesterday));

        // 2. Chart Data (7 ngày gần nhất)
        List<Object[]> dailyStats = orderRepo.getDailyStats(now.minusDays(6));
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revData = new ArrayList<>();
        List<Long> ordData = new ArrayList<>();

        // Fill data logic (đơn giản hóa)
        for (Object[] row : dailyStats) {
            labels.add(row[0].toString());
            revData.add((BigDecimal) row[1]);
            ordData.add((Long) row[2]);
        }

        // 3. Top Products & Low Stock
        List<BookResponse> topSelling = bookRepo.findAll(PageRequest.of(0, 5, Sort.by("soldQuantity").descending())) // Giả sử có field soldQuantity
                .map(mapper::toBookResponse).getContent();

        List<BookResponse> lowStock = bookRepo.findLowStockBooks(10, PageRequest.of(0, 5))
                .stream()                            // 1. Tạo luồng dữ liệu
                .map(mapper::toBookResponse)         // 2. Chuyển đổi từng Book thành BookResponse
                .toList();

        // 4. Operations & Funnel (Giả lập số liệu View/Cart vì chưa có tracking thật)
        long totalOrders = orderRepo.count();
        long cancelledOrders = orderRepo.countOrdersByStatus(OrderStatus.CANCELLED); // Cần thêm hàm này ở Repo
        double cancelRate = totalOrders == 0 ? 0 : ((double) cancelledOrders / totalOrders) * 100;

        return AdvancedDashboardResponse.builder()
                .revenueToday(revToday)
                .revenueGrowth(revGrowth)
                .ordersToday(ordersToday)
                .ordersGrowth(orderGrowth)
                .activeUsers(userRepo.count()) // Tạm lấy tổng user
                .conversionRate(2.5) // Hardcode demo hoặc tính logic: orders / activeUsers * 100
                .chartLabels(labels)
                .revenueData(revData)
                .orderData(ordData)
                .topSellingBooks(topSelling)
                .lowStockBooks(lowStock)
                .cancellationRate(cancelRate)
                .build();
    }

    private double calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) return 100.0;
        return current.subtract(previous).divide(previous, 2, RoundingMode.HALF_UP).doubleValue() * 100;
    }
}