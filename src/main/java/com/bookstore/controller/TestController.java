package com.bookstore.controller;

import com.bookstore.entity.Order;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    public TestController(OrderRepository orderRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/database")
    public ResponseEntity<?> testDatabase() {
        log.info("=== TEST DATABASE CONNECTION ===");

        try {
            // Test user count
            long userCount = userRepo.count();
            log.info("User count: {}", userCount);

            // Test order count
            long orderCount = orderRepo.count();
            log.info("Order count: {}", orderCount);

            // Test simple order query
            List<Order> orders = orderRepo.findAll();
            log.info("Simple findAll orders: {}", orders.size());

            Map<String, Object> result = Map.of(
                    "users", userCount,
                    "orders", orderCount,
                    "simpleQueryWorks", !orders.isEmpty()
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("DATABASE TEST FAILED: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Database connection failed: " + e.getMessage()));
        }
    }

    @GetMapping("/orders-simple")
    public ResponseEntity<?> getOrdersSimple() {
        log.info("=== TEST SIMPLE ORDERS ===");

        try {
            List<Order> orders = orderRepo.findAll();

            List<Map<String, Object>> orderList = orders.stream()
                    .map(order -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", order.getId());
                        map.put("status", order.getStatus().toString());
                        map.put("orderDate", order.getOrderDate());
                        map.put("totalAmount", order.getTotalAmount());
                        map.put("hasUser", order.getUser() != null);
                        map.put("hasItems", order.getOrderItems() != null && !order.getOrderItems().isEmpty());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", orderList.size(),
                    "orders", orderList
            ));

        } catch (Exception e) {
            log.error("SIMPLE ORDERS FAILED: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Simple orders failed: " + e.getMessage()));
        }
    }
    @GetMapping("/orders-with-joins")
    public ResponseEntity<?> testOrdersWithJoins() {
        log.info("=== TEST ORDERS WITH JOINS ===");

        try {
            // Test repository method với JOIN FETCH
            List<Order> orders = orderRepo.findAllByOrderByOrderDateDesc();

            List<Map<String, Object>> result = orders.stream()
                    .map(order -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", order.getId());
                        map.put("status", order.getStatus());
                        map.put("username", order.getUsername()); // Dùng getter mới
                        map.put("orderDate", order.getOrderDate());
                        map.put("totalAmount", order.getTotalAmount());

                        // Kiểm tra eager loading
                        map.put("hasUserEager", order.getUser() != null);
                        map.put("hasItemsEager", order.getOrderItems() != null && !order.getOrderItems().isEmpty());
                        map.put("itemsCount", order.getOrderItems() != null ? order.getOrderItems().size() : 0);

                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", result.size(),
                    "orders", result
            ));

        } catch (Exception e) {
            log.error("ORDERS WITH JOINS FAILED: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Orders with joins failed: " + e.getMessage()));
        }
    }
}
