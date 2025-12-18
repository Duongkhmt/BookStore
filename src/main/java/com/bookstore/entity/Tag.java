//package com.bookstore.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//@Table(name = "tags")
//@Getter // Thay @Data bằng @Getter
//@Setter
//@NoArgsConstructor
//public class Tag {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true, length = 50)
//    private String name;
//
//    @Column(length = 100)
//    private String description;
//
//    @Column(name = "tag_type")
//    private String tagType; // "skill", "technology", "methodology", etc.
//
//    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
//    private Set<Book> books = new HashSet<>();
//}
package com.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "tag_type")
    private String tagType;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @JsonIgnore // Ngắt vòng lặp JSON: Không serialize danh sách sách khi lấy Tag
    private Set<Book> books = new HashSet<>();

    // --- FIX EQUALS & HASHCODE ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return id != null && id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Tag{id=" + id + ", name='" + name + "'}";
    }
}