//    package com.bookstore.repository;
//
//    import com.bookstore.entity.Book;
//    import com.bookstore.entity.DifficultyLevel;
//    import org.springframework.data.jpa.repository.EntityGraph;
//    import org.springframework.data.jpa.repository.JpaRepository;
//    import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//    import org.springframework.data.jpa.repository.Query;
//    import org.springframework.stereotype.Repository;
//
//    import java.awt.print.Pageable;
//    import java.util.List;
//    import java.util.Optional;
//
//    @Repository
//    public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
//
//    boolean existsByIsbn(String isbn);
//
//    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//    List<Book> findAllByCategoryId(Long categoryId);
//
//        // Tìm theo tên thể loại - Spring Data JPA tự generate query
//    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//    List<Book> findByCategoryNameContainingIgnoreCase(String categoryName);
//
//        // Tìm kiếm tổng quát - Spring Data JPA tự generate query
//    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
//    String title, String author, String categoryName);
//
//    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//    List<Book> findByTitleContainingIgnoreCase(String title);
//
//    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//    List<Book> findByAuthorContainingIgnoreCase(String author);
//
//    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//    Optional<Book> findById(Long id);
//
//    List<Book> findByDifficultyLevel(DifficultyLevel difficultyLevel);
//
//        @Query(value = """
//    SELECT b.*
//    FROM books b
//    LEFT JOIN (
//        SELECT book_id,
//               AVG(COALESCE(rating, 0)) AS avg_rating,
//               COUNT(id) AS review_count
//        FROM book_reviews
//        GROUP BY book_id
//    ) r ON b.id = r.book_id
//    ORDER BY r.avg_rating DESC, r.review_count DESC
//    LIMIT 8
//    """,
//                nativeQuery = true)
//        List<Book> findFeaturedBooks();
//}
package com.bookstore.repository;

import com.bookstore.entity.Book;
import com.bookstore.entity.DifficultyLevel;
import org.springframework.data.domain.Page; // Dùng Page
import org.springframework.data.domain.Pageable; // IMPORT ĐÚNG CÁI NÀY
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    boolean existsByIsbn(String isbn);

    // Thêm Pageable vào tham số, trả về Page
    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    Page<Book> findAllByCategoryId(Long categoryId, Pageable pageable);

    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    Page<Book> findByCategoryNameContainingIgnoreCase(String categoryName, Pageable pageable);

    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
            String title, String author, String categoryName, Pageable pageable);

    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);


    // Với logic đề xuất sách, có thể giữ List nhưng nên limit số lượng
    List<Book> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    // Query này nặng, nên dùng Cache (xem phần hướng dẫn bên dưới)
    @Query(value = """
        SELECT b.*
        FROM books b
        LEFT JOIN (
            SELECT book_id,
                   AVG(COALESCE(rating, 0)) AS avg_rating,
                   COUNT(id) AS review_count
            FROM book_reviews
            GROUP BY book_id
        ) r ON b.id = r.book_id
        ORDER BY r.avg_rating DESC, r.review_count DESC
        LIMIT 8
        """, nativeQuery = true)
    List<Book> findFeaturedBooks();

    @Query("SELECT DISTINCT b FROM Book b JOIN b.tags t WHERE t.id IN :tagIds AND b.id != :currentBookId")
    List<Book> findRelatedByTagIds(@Param("tagIds") Set<Long> tagIds, @Param("currentBookId") Long currentBookId);

    // 2. Tìm sách liên quan theo danh sách ID của Topics
    @Query("SELECT DISTINCT b FROM Book b JOIN b.topics t WHERE t.id IN :topicIds AND b.id != :currentBookId")
    List<Book> findRelatedByTopicIds(@Param("topicIds") Set<Long> topicIds, @Param("currentBookId") Long currentBookId);

    // 3. Tìm sách cùng độ khó (Difficulty Level) - tối ưu hơn so với fetch hết
    List<Book> findTop10ByDifficultyLevelAndIdNot(DifficultyLevel difficultyLevel, Long currentBookId);
}
