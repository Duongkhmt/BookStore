package com.bookstore.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;
@Data
public class CreateOrderRequest {
    @NotEmpty @Valid private List<CartItemRequest> items;
    @NotBlank private String shippingAddress;
}