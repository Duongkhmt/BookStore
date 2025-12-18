//// src/main/java/com/bookstore/entity/Book.java
//package com.bookstore.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//@Table(name = "books")
//@Getter // Thay @Data bằng @Getter
//@Setter
//@EqualsAndHashCode(exclude = {"category", "publisher", "tags", "topics", "reviews", "orderItems", "inventoryHistory"})
//@ToString(exclude = {"category", "publisher", "tags", "topics", "reviews", "orderItems", "inventoryHistory"})
//@NamedEntityGraph(
//        name = "book-with-details",
//        attributeNodes = {
//                @NamedAttributeNode("category"),
//                @NamedAttributeNode("publisher"),
//                @NamedAttributeNode("tags"),
//                @NamedAttributeNode("topics"),
//                @NamedAttributeNode("reviews")
//        }
//)
//public class Book {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 255)
//    private String title;
//
//    @Column(nullable = false, length = 100)
//    private String author;
//
//    @Column(nullable = false, unique = true, length = 20)
//    private String isbn;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal price;
//
//    @Column(nullable = false)
//    private Integer stockQuantity;
//
//    // Thông tin mở rộng
//    @Enumerated(EnumType.STRING)
//    @Column(name = "difficulty_level")
//    private DifficultyLevel difficultyLevel;
//
//    @Column(columnDefinition = "TEXT")
//    private String summary;
//
//    @Column(name = "publication_year")
//    private Integer publicationYear;
//
//    @Column(name = "page_count")
//    private Integer pageCount;
//
//    // Quan hệ
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category category;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "publisher_id", nullable = false)
//    private Publisher publisher;
//
////    @ManyToMany(fetch = FetchType.LAZY)
////    @JoinTable(
////            name = "book_tags",
////            joinColumns = @JoinColumn(name = "book_id"),
////            inverseJoinColumns = @JoinColumn(name = "tag_id")
////    )
////    private Set<Tag> tags = new HashSet<>();
////
////    @ManyToMany(fetch = FetchType.LAZY)
////    @JoinTable(
////            name = "book_topics",
////            joinColumns = @JoinColumn(name = "book_id"),
////            inverseJoinColumns = @JoinColumn(name = "topic_id")
////    )
////    private Set<Topic> topics = new HashSet<>();
////
////    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
////    private Set<BookReview> reviews = new HashSet<>();
////
////    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
////    private Set<OrderItem> orderItems = new HashSet<>();
////
////    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
////    private Set<Inventory> inventoryHistory = new HashSet<>();
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "book_tags",
//            joinColumns = @JoinColumn(name = "book_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id")
//    )
//    @JsonIgnoreProperties("books") // Quan trọng: Khi serialize Tag, không serialize ngược lại books
//    private Set<Tag> tags = new HashSet<>();
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "book_topics",
//            joinColumns = @JoinColumn(name = "book_id"),
//            inverseJoinColumns = @JoinColumn(name = "topic_id")
//    )
//    @JsonIgnoreProperties("books") // Quan trọng: Khi serialize Topic, không serialize ngược lại books
//    private Set<Topic> topics = new HashSet<>();
//
//    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties("book") // Chặn loop từ review
//    private Set<BookReview> reviews = new HashSet<>();
//
//    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties("book")
//    private Set<OrderItem> orderItems = new HashSet<>();
//
//    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties("book")
//    private Set<Inventory> inventoryHistory = new HashSet<>();
//}

package com.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@NamedEntityGraph(
        name = "book-with-details",
        attributeNodes = {
                @NamedAttributeNode("category"),
                @NamedAttributeNode("publisher"),
                @NamedAttributeNode("tags"),
                @NamedAttributeNode("topics")
        }
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "page_count")
    private Integer pageCount;

    // Thêm trường image nếu database có cột này, nếu không thì bỏ qua
    // private String image;

    // --- QUAN HỆ ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"books", "hibernateLazyInitializer", "handler"})
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    @JsonIgnoreProperties({"books", "hibernateLazyInitializer", "handler"})
    private Publisher publisher;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_tags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnoreProperties("books")
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_topics",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @JsonIgnoreProperties("books")
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("book")
    private Set<BookReview> reviews = new HashSet<>();

    @OneToMany(mappedBy = "book")
    @JsonIgnoreProperties("book")
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(mappedBy = "book")
    @JsonIgnoreProperties("book")
    private Set<Inventory> inventoryHistory = new HashSet<>();

    // --- QUAN TRỌNG: VIẾT THỦ CÔNG EQUALS & HASHCODE THEO ID ---
    // Điều này ngăn chặn vòng lặp vô tận khi đưa Book vào HashSet

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id != null && id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // Luôn trả về constant để đảm bảo tính nhất quán trong Set
    }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "'}"; // Chỉ in ID và Title
    }
}