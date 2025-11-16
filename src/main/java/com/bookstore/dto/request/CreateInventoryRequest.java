package com.bookstore.dto.request;

import com.bookstore.entity.InventoryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInventoryRequest {
    @NotNull(message = "ID sách không được để trống")
    private Long bookId;

    @NotNull(message = "Loại giao dịch không được để trống (IN/OUT)")
    private InventoryType type;

    @NotNull(message = "Số lượng thay đổi không được để trống")
    @Min(value = 1, message = "Số lượng thay đổi phải lớn hơn 0")
    private Integer quantityChange;

    @NotNull(message = "Lý do không được để trống")
    private String reason;
}