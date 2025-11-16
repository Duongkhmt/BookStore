package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "receipt_items")
@Data
@NoArgsConstructor
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    @ToString.Exclude // <-- GIẢI PHÁP
    @EqualsAndHashCode.Exclude // <-- GIẢI PHÁP
    private Receipt receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "import_price", nullable = false)
    private BigDecimal importPrice; // Giá nhập

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal; // quantity * importPrice

    public ReceiptItem(Receipt receipt, Book book, Integer quantity, BigDecimal importPrice) {
        this.receipt = receipt;
        this.book = book;
        this.quantity = quantity;
        this.importPrice = importPrice;
        this.subtotal = importPrice.multiply(BigDecimal.valueOf(quantity));
    }
}