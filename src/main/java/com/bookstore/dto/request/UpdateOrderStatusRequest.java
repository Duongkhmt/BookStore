package com.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class UpdateOrderStatusRequest {
    // Expected values: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    @NotBlank private String newStatus;
    private String note;
}