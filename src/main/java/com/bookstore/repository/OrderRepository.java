package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.entity.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.totalAmount) " +
            "FROM Order o " +
            "WHERE o.status = com.bookstore.entity.OrderStatus.DELIVERED " +
            "AND o.orderDate >= :startDate " +
            "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) " +
            "ORDER BY YEAR(o.orderDate) ASC, MONTH(o.orderDate) ASC")
    List<Object[]> getRevenueByMonth(@Param("startDate") LocalDateTime startDate);

    // Doanh thu trong khoảng thời gian
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED'")
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Số đơn trong khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    long countOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Dữ liệu biểu đồ (Group by Date) - Native Query cho hiệu suất cao
    // Lưu ý: Cú pháp này dành cho MySQL/Postgres. H2 có thể khác chút.
    @Query("SELECT DATE(o.orderDate) as date, SUM(o.totalAmount) as rev, COUNT(o) as cnt " +
            "FROM Order o " +
            "WHERE o.orderDate >= :startDate AND o.status <> 'CANCELLED' " +
            "GROUP BY DATE(o.orderDate) " +
            "ORDER BY DATE(o.orderDate) ASC")
    List<Object[]> getDailyStats(@Param("startDate") LocalDateTime startDate);

    // Đếm theo trạng thái
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countOrdersByStatus(@Param("status") OrderStatus status);


}