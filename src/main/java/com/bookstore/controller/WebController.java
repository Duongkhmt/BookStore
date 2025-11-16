package com.bookstore.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {


    @GetMapping("/login")
    public String login() {
        return "login";
        // templates/login.html
    }
    @GetMapping("/register")
    public String registerPage() {
        return "register"; // templates/register.html
    }

    @GetMapping("/orders")
    public String ordersPage(Model model) {
        model.addAttribute("pageTitle", "Đơn hàng của tôi");
        return "orders"; // Thymeleaf sẽ render layout.html + orders.html
    }
    @GetMapping({"/admin", "/admin/dashboard"})
    public String adminPage(Model model) {
        model.addAttribute("pageTitle", "Quản trị hệ thống");
        return "admin/dashboard";
    }

    @GetMapping("/catalog")
    public String catalogPage(Model model) {
        model.addAttribute("pageTitle", "Danh mục sách");
        return "catalog"; // render toàn bộ catalog.html
    }
    // ✅ THÊM: Route cho trang giỏ hàng
    @GetMapping("/cart")
    public String cartPage(Model model) {
        model.addAttribute("pageTitle", "Giỏ hàng");
        return "cart";
    }
}