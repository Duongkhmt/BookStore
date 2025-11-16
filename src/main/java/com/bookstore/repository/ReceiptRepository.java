package com.bookstore.repository;


import com.bookstore.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    @EntityGraph(attributePaths = {"createdBy", "items", "items.book"})
    List<Receipt> findAllByOrderByReceiptDateDesc();

    @EntityGraph(attributePaths = {"createdBy", "items", "items.book"})
    Optional<Receipt> findById(Long id);

    List<Receipt> findBySupplierIdOrderByReceiptDateDesc(Long supplierId);
}
