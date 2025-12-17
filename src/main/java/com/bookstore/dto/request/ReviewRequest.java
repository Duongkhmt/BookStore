package com.bookstore.dto.request;


// Dùng record cho gọn (Java 14+)
public record ReviewRequest(Integer rating, String comment) {}