package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 100)
    private String description;

    @Column(name = "tag_type")
    private String tagType; // "skill", "technology", "methodology", etc.

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();
}
