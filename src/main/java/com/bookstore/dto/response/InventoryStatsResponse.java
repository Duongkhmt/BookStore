package com.bookstore.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class InventoryStatsResponse {
    private long totalBooks;
    private BigDecimal totalValue;
    private long lowStockCount;
    private long outOfStockCount;
}