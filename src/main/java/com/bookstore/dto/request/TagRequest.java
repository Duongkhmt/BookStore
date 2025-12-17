package com.bookstore.dto.request;

import lombok.Data;

@Data
public class TagRequest {
    private String name;
    private String description;
    private String tagType; // Chỉ dùng cho Tag
}
