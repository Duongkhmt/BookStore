// src/main/java/com/bookstore/entity/Book.java
package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Data
@EqualsAndHashCode(exclude = {"category", "publisher", "tags", "topics", "reviews", "orderItems", "inventoryHistory"})
@ToString(exclude = {"category", "publisher", "tags", "topics", "reviews", "orderItems", "inventoryHistory"})
@NamedEntityGraph(
        name = "book-with-details",
        attributeNodes = {
                @NamedAttributeNode("category"),
                @NamedAttributeNode("publisher"),
                @NamedAttributeNode("tags"),
                @NamedAttributeNode("topics"),
                @NamedAttributeNode("reviews")
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

    @Column(nullable = false)
    private Integer stockQuantity;

    // Thông tin mở rộng
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "page_count")
    private Integer pageCount;

    // Quan hệ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_tags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_topics",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookReview> reviews = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inventory> inventoryHistory = new HashSet<>();
}