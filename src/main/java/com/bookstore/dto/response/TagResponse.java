package com.bookstore.dto.response;

import lombok.Data;

@Data
public class TagResponse {
    private Long id;
    private String name;
    private String tagType;
    private String description;
}