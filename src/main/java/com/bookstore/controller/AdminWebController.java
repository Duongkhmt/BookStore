package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminWebController {
    @GetMapping("")
    public String adminHome() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Tổng quan hệ thống");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("pageTitle", "Quản lý người dùng");
        return "admin/users";
    }

    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("pageTitle", "Quản lý sách");
        return "admin/books";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("pageTitle", "Quản lý thể loại");
        return "admin/category";
    }

    @GetMapping("/publishers")
    public String publishers(Model model) {
        model.addAttribute("pageTitle", "Quản lý nhà xuất bản");
        return "admin/publishers";
    }

    @GetMapping("/suppliers")
    public String suppliers(Model model) {
        model.addAttribute("pageTitle", "Quản lý nhà cung cấp");
        return "admin/suppliers";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("pageTitle", "Quản lý đơn hàng");
        return "admin/orders";
    }

    @GetMapping("/receipts")
    public String receipts(Model model) {
        model.addAttribute("pageTitle", "Quản lý phiếu nhập kho");
        return "admin/receipts";
    }

    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("pageTitle", "Quản lý tồn kho");
        return "admin/inventory";
    }
}

