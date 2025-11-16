package com.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Data
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "order-items")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "order-tracking")
    @OrderBy("timestamp DESC")
    private List<OrderTracking> trackingHistory = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference(value = "order-payment")
    private Payment payment;

    public Order(User user, String shippingAddress, OrderStatus status, LocalDateTime orderDate) {
        this.user = user;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.orderDate = orderDate;
    }

    // Getter cho username
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    // 🚀 THÊM: Helper method để thêm tracking
    public void addTracking(OrderStatus status, String note) {
        OrderTracking tracking = new OrderTracking();
        tracking.setOrder(this);
        tracking.setStatus(status);
        tracking.setNote(note);
        tracking.setTimestamp(LocalDateTime.now());
        this.trackingHistory.add(tracking);
    }

    // 🚀 THÊM: Helper method để tính tổng tiền
    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(item -> item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
