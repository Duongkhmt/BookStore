package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "books")
@Data
@EqualsAndHashCode(exclude = {"category", "publisher", "orderItems", "inventoryHistory"})
@ToString(exclude = {"category", "publisher", "orderItems", "inventoryHistory"})
@NamedEntityGraph(
        name = "book-with-details",
        attributeNodes = {
                @NamedAttributeNode("category"),
                @NamedAttributeNode("publisher")
        }
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // THÊM/GIỮ LẠI: Số lượng tồn kho hiện tại
    @Column(nullable = false)
    private Integer stockQuantity;

    // SỬA: Thêm cascade PERSIST và MERGE
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher; //

    // Mối quan hệ với OrderItem (Khi được đặt hàng)
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    // MỚI: Mối quan hệ với Inventory (Lịch sử tồn kho)
    // Khi một Book bị xóa, các bản ghi Inventory liên quan cũng bị xóa (cascade)
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inventory> inventoryHistory; // << THÊM TRƯỜNG NÀY
}