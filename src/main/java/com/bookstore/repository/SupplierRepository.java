package com.bookstore.repository;

import com.bookstore.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByName(String name);
    List<Supplier> findByIsActiveTrueOrderByName();
}