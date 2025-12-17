package com.bookstore.repository;

import com.bookstore.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// package com.bookstore.repository.TopicRepository

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Topic> findByNameContainingIgnoreCase(String keyword);
}