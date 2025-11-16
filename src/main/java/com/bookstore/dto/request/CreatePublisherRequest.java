package com.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePublisherRequest {
    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    private String name;
    private String address;
    private String phone;
}