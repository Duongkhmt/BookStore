package com.bookstore.service;


import com.bookstore.dto.response.RevenueReportResponse;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderStatus;
import com.bookstore.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public RevenueReportResponse generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Lấy tất cả các đơn hàng đã giao thành công trong khoảng thời gian
        // (Trong thực tế, nên dùng JPQL/Criteria query để tối ưu hiệu suất)
        List<Order> deliveredOrders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderDate().isAfter(startDateTime) && order.getOrderDate().isBefore(endDateTime))
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .toList();

        BigDecimal totalRevenue = deliveredOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = deliveredOrders.size();

        RevenueReportResponse report = new RevenueReportResponse();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalRevenue(totalRevenue);
        report.setTotalDeliveredOrders(totalOrders);

        return report;
    }

    // Các phương thức thống kê khác như: Top Selling Books, User Activity...
}