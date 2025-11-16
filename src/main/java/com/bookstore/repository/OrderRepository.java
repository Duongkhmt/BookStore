package com.bookstore.repository;

import com.bookstore.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        // Phiên bản tối ưu với EntityGraph
        @EntityGraph(attributePaths = {
                "orderItems",
                "orderItems.book",
                "payment"
        })
        @Query("SELECT o FROM Order o WHERE o.user.username = :username ORDER BY o.orderDate DESC")
        List<Order> findByUserUsernameOrderByOrderDateDesc(@Param("username") String username);

        @EntityGraph(attributePaths = {
                "user", // Chỉ lấy user thôi, không cần trackingHistory
                "orderItems",
                "orderItems.book",
                "payment"
        })
        @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
        List<Order> findAllByOrderByOrderDateDesc();

        @EntityGraph(attributePaths = {
                "user",
                "orderItems",
                "orderItems.book",
                "payment",
        })
        @Query("SELECT DISTINCT o FROM Order o WHERE o.id = :id")
        Optional<Order> findWithDetailsById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findWithUserById(@Param("id") Long id);

}