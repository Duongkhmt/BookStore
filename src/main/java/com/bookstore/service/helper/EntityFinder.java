package com.bookstore.service.helper;

import com.bookstore.entity.*;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.*;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class EntityFinder {

    private final BookRepository bookRepo;
    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final OrderTrackingRepository orderTrackingRepo;
    // === BỔ SUNG REPO CHO BOOKSERVICE ===
    private final CategoryRepository categoryRepo;
    private final PublisherRepository publisherRepo;
    private final SupplierRepository supplierRepo;

    public EntityFinder(BookRepository bookRepo, OrderRepository orderRepo,
                        PaymentRepository paymentRepo, UserRepository userRepo,
                        OrderTrackingRepository orderTrackingRepo,
                        // Bổ sung 2 repo này vào constructor
                        CategoryRepository categoryRepo,
                        PublisherRepository publisherRepo, SupplierRepository supplierRepo) {

        this.bookRepo = bookRepo;
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.orderTrackingRepo = orderTrackingRepo;

        // Bổ sung
        this.categoryRepo = categoryRepo;
        this.publisherRepo = publisherRepo;
        this.supplierRepo = supplierRepo;

        log.info("EntityFinder initialized");
    }

    public Book findBook(Long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sách ID: " + id));
    }

    @Transactional(readOnly = true)
    public Order findOrder(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy đơn hàng ID: " + id));
    }

    public Payment findPayment(Long id) {
        return paymentRepo.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy thanh toán ID: " + id));
    }

    public Order findOrderWithDetails(Long id) {
        log.info("Finding order with details: {}", id);
        try {
            Optional<Order> orderOpt = orderRepo.findWithDetailsById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                log.info("Found order: {}", id);
                return order;
                // 🚀 KHÔNG cần fetch trackingHistory riêng nữa
            } else {
                log.warn("Order not found: {}", id);
                throw new ApplicationException(ErrorCode.ORDER_NOT_FOUND,
                        "Không tìm thấy đơn hàng với ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error finding order {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public User findUser(String username) {
        log.info("Finding user: {}", username);
        try {
            Optional<User> userOpt = userRepo.findByUsername(username);
            if (userOpt.isPresent()) {
                log.info("Found user: {}", username);
                return userOpt.get();
            } else {
                log.warn("User not found: {}", username);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy người dùng: " + username);
            }
        } catch (Exception e) {
            log.error("Error finding user {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }
    public Category findCategoryById(Long id) {
        log.info("Finding category by ID: {}", id);
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy danh mục ID: " + id));
        log.info("Category found: {} - {}", category.getId(), category.getName());
        return category;
    }

    public Publisher findPublisherById(Long id) {
        log.info("Finding publisher by ID: {}", id);
        Publisher publisher = publisherRepo.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy nhà xuất bản ID: " + id));
        log.info("Publisher found: {} - {}", publisher.getId(), publisher.getName());
        return publisher;
    }
    // Thêm methods mới
    public Supplier findSupplierById(Long id) {
        return supplierRepo.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy nhà cung cấp ID: " + id));
    }

}

