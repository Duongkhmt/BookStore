package com.bookstore.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {
        // Lấy URI hiện tại và gửi xuống view với tên là "requestURI"
        model.addAttribute("requestURI", request.getRequestURI());
    }
}
