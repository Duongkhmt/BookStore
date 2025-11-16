    package com.bookstore.repository;

    import com.bookstore.entity.Book;
    import org.springframework.data.jpa.repository.EntityGraph;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface BookRepository extends JpaRepository<Book, Long> {
//
//        boolean existsByIsbn(String isbn);
//
//        @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//        List<Book> findAllByCategoryId(Long categoryId);
//
//        @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//        List<Book> findAll();
//
//        @EntityGraph(value = "book-with-details", type = EntityGraph.EntityGraphType.LOAD)
//        Optional<Book> findById(Long id);
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
}