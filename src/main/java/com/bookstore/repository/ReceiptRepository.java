package com.bookstore.repository;


import com.bookstore.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    @EntityGraph(attributePaths = {"createdBy", "items", "items.book"})
    List<Receipt> findAllByOrderByReceiptDateDesc();
    // Đếm số phiếu nhập theo NCC
    long countBySupplierId(Long supplierId);

    // Tính tổng tiền nhập theo NCC (Xử lý trường hợp null nếu chưa có phiếu nào)
    @Query("SELECT COALESCE(SUM(r.totalAmount), 0) FROM Receipt r WHERE r.supplier.id = :supplierId")
    BigDecimal sumTotalImportBySupplierId(@Param("supplierId") Long supplierId);
}
