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
//@Table(name = "topics")
//@Getter // Thay @Data bằng @Getter
//@Setter
//@NoArgsConstructor
//public class Topic {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true, length = 100)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @ManyToMany(mappedBy = "topics", fetch = FetchType.LAZY)
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
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "topics", fetch = FetchType.LAZY)
    @JsonIgnore // Ngắt vòng lặp JSON
    private Set<Book> books = new HashSet<>();

    // --- FIX EQUALS & HASHCODE ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;
        Topic topic = (Topic) o;
        return id != null && id.equals(topic.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Topic{id=" + id + ", name='" + name + "'}";
    }
}