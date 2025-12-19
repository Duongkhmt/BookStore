package com.bookstore.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AdvancedDashboardResponse {
    // 1. Overview KPIs
    private BigDecimal revenueToday;
    private double revenueGrowth; // % so với hôm qua

    private long ordersToday;
    private double ordersGrowth; // % so với hôm qua

    private long activeUsers; // User có tương tác/mua hàng
    private double conversionRate; // Tỉ lệ mua hàng

    // 2. Chart Data (Dual Axis)
    private List<String> chartLabels; // Ngày
    private List<BigDecimal> revenueData; // Doanh thu
    private List<Long> orderData; // Số đơn

    // 3. Funnel Data
    private long viewCount;
    private long addToCartCount;
    private long paymentSuccessCount;

    // 4. Product Performance
    private List<BookResponse> topSellingBooks;
    private List<BookResponse> lowStockBooks;

    // 5. Operations
    private Map<String, Long> orderStatusBreakdown; // PENDING: 5, SHIPPED: 10...
    private double cancellationRate; // Tỉ lệ hủy
}