//package com.bookstore.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//import lombok.AllArgsConstructor;
//import java.util.Set;
//
//@Entity
//@Table(name = "publishers")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(exclude = "books") // Loại trừ books để tránh StackOverflow khi gọi toString()
//public class Publisher {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "name", nullable = false, unique = true)
//    private String name;
//
//    @Column(name = "address")
//    private String address;
//
//    @Column(name = "phone")
//    private String phone;
//
//    // Mối quan hệ One-to-Many với Book
//    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Book> books;
//}
package com.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "publishers")
@Getter // Dùng Getter/Setter rời thay vì @Data
@Setter
@NoArgsConstructor
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
    @JsonIgnore // <--- QUAN TRỌNG: Ngắt vòng lặp JSON
    private Set<Book> books;

    // --- VIẾT LẠI HASHCODE & EQUALS CHỈ DỰA TRÊN ID ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publisher)) return false;
        Publisher publisher = (Publisher) o;
        return id != null && id.equals(publisher.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Publisher{id=" + id + ", name='" + name + "'}";
    }
}