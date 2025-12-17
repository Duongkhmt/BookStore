package com.bookstore.repository;

import com.bookstore.entity.BookReview;
import org.springframework.data.domain.Pageable; // Import cái này để dùng limit
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {

    // JPA tự động tạo query sắp xếp
    List<BookReview> findByBookIdOrderByCreatedAtDesc(Long bookId);

    Optional<BookReview> findByBookIdAndUserId(Long bookId, Long userId);

    // JPA tự động tạo query đếm (Đã xóa @Query thủ công)
    long countByBookId(Long bookId);
    // JPA không hỗ trợ AVG qua tên hàm trực tiếp, nên giữ lại Query này là tốt nhất
    @Query("SELECT AVG(r.rating) FROM BookReview r WHERE r.book.id = :bookId")
    Optional<Double> findAverageRatingByBookId(@Param("bookId") Long bookId);

}