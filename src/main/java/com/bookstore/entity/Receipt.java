package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt_code", unique = true, nullable = false, length = 50)
    private String receiptCode; // Mã phiếu: PH-2025-001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // Người tạo phiếu

    @Column(name = "receipt_date", nullable = false)
    private LocalDateTime receiptDate = LocalDateTime.now();

    // ✅ THAY ĐỔI: Dùng Supplier entity thay vì String
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id") // Tên cột mới
    private Supplier supplier;


    @Column(name = "note", columnDefinition = "TEXT")
    private String note; // Ghi chú

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // <-- GIẢI PHÁP
    @EqualsAndHashCode.Exclude // <-- GIẢI PHÁP
    private Set<ReceiptItem> items = new HashSet<>();

    // Helper method để generate mã phiếu
    public static String generateReceiptCode() {
        return "PH-" + LocalDateTime.now().getYear() + "-"
                + String.format("%04d", (int)(Math.random() * 10000));
    }
}