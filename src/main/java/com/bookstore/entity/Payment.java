package com.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết One-to-One với Order
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    @JsonBackReference(value = "order-payment")
    private Order order;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Ví dụ: "VNPAY", "COD", "Credit Card"

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // Số tiền thanh toán thực tế (nên khớp với Order.totalAmount)

    @Column(name = "transaction_id", unique = true)
    private String transactionId; // Mã giao dịch của bên thứ ba (ví dụ: ngân hàng, VNPAY)

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status; // Enum: PENDING, COMPLETED, FAILED, REFUNDED

    @PrePersist
    protected void onCreate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }
}