    package com.bookstore.repository;

    import com.bookstore.entity.Book;
    import com.bookstore.entity.DifficultyLevel;
    import org.springframework.data.jpa.repository.EntityGraph;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.stereotype.Repository;

    import java.awt.print.Pageable;
    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    boolean existsByIsbn(String isbn);

    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    List<Book> findAllByCategoryId(Long categoryId);

        // Tìm theo tên thể loại - Spring Data JPA tự generate query
    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    List<Book> findByCategoryNameContainingIgnoreCase(String categoryName);

        // Tìm kiếm tổng quát - Spring Data JPA tự generate query
    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
    String title, String author, String categoryName);

    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    List<Book> findByTitleContainingIgnoreCase(String title);

    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    List<Book> findByAuthorContainingIgnoreCase(String author);

    @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Book> findById(Long id);

    List<Book> findByDifficultyLevel(DifficultyLevel difficultyLevel);

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
    """,
                nativeQuery = true)
        List<Book> findFeaturedBooks();
}