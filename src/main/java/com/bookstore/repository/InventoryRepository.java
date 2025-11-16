package com.bookstore.repository;

import com.bookstore.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Có thể thêm các phương thức tìm kiếm theo Book
     List<Inventory> findByBookIdOrderByTimestampDesc(Long bookId);
}