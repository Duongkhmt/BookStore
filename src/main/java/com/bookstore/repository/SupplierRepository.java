package com.bookstore.repository;

import com.bookstore.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByName(String name);
    boolean existsByName(String name);

    // Không cần @Query - Spring Data JPA tự generate
    List<Supplier> findAllByOrderByName();
    List<Supplier> findByIsActiveTrueOrderByName();
    List<Supplier> findByNameContainingIgnoreCase(String name);
    List<Supplier> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}