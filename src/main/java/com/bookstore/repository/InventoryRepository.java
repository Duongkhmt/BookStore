package com.bookstore.repository;

import com.bookstore.entity.Inventory;
import com.bookstore.entity.InventoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Có thể thêm các phương thức tìm kiếm theo Book
     List<Inventory> findByBookIdOrderByTimestampDesc(Long bookId);
    // Thêm các method này vào InventoryRepository
    List<Inventory> findAllByOrderByTimestampDesc();
    List<Inventory> findByTypeOrderByTimestampDesc(InventoryType type);
    // Tính tổng giá trị tồn kho hiện tại (Price * Stock) của TẤT CẢ sách
    @Query("SELECT COALESCE(SUM(b.price * b.stockQuantity), 0) FROM Book b")
    BigDecimal calculateTotalInventoryValue();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.stockQuantity = 0")
    long countOutOfStockBooks();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.stockQuantity > 0 AND b.stockQuantity < 10")
    long countLowStockBooks();
}