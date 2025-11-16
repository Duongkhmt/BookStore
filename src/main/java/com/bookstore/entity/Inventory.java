package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết Many-to-One với Book
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InventoryType type; // Loại giao dịch: NHAP (IN), XUAT (OUT)

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange; // Số lượng thay đổi (luôn là số dương)

    @Column(name = "reason")
    private String reason; // Lý do (ví dụ: "Nhập kho lô hàng mới", "Xuất kho cho đơn hàng #123")

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // ✅ Constructor đầy đủ khớp với dòng bạn đang gọi
    public Inventory(Book book, Integer quantityChange, InventoryType type, String reason, LocalDateTime timestamp) {
        this.book = book;
        this.quantityChange = quantityChange;
        this.type = type;
        this.reason = reason;
        this.timestamp = timestamp;
    }
}