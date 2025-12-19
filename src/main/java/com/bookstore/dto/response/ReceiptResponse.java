package com.bookstore.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class ReceiptResponse {
    private Long id;
    private String receiptCode;
    private LocalDateTime receiptDate;
    private String supplier;
    private String note;
    private BigDecimal totalAmount;
    private String createdByUsername;
    private List<ReceiptItemResponse> items = new ArrayList<>();
    private Long supplierId;
    // 1. Dùng để hiển thị trên bảng (VD: "5 cuốn") mà không cần tải chi tiết
    private int itemCount;
}
