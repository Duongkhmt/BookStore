package com.bookstore.dto.response;

import lombok.Data;

@Data
public class PublisherResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
}