package com.bookstore.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateReceiptRequest {

    @NotNull(message = "Nhà cung cấp không được để trống")
    private Long supplierId; // ✅ THÊM supplierId


    private String note; // Ghi chú (optional)

    @NotEmpty(message = "Danh sách sách nhập không được để trống")
    @Valid
    private List<ReceiptItemRequest> items;

}
