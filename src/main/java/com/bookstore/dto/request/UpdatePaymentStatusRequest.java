package com.bookstore.dto.request;

import lombok.Data;

@Data
public class UpdatePaymentStatusRequest {

    // Trạng thái thanh toán mới (e.g., "COMPLETED", "FAILED", "REFUNDED")
    private String newStatus;

    // ID giao dịch thực tế từ cổng thanh toán (nếu cập nhật sau khi tạo)
    private String transactionId;
}