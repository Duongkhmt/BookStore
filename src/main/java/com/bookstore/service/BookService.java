
package com.bookstore.service;

import com.bookstore.dto.request.CreateBookRequest;
import com.bookstore.dto.request.UpdateBookRequest;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.entity.Publisher;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.BookRepository;
// Bỏ: CategoryRepository và PublisherRepository
import com.bookstore.service.helper.EntityMapper;   // THÊM
import com.bookstore.service.helper.EntityFinder; // THÊM
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final EntityMapper entityMapper;     // THÊM: Dùng EntityMapper chung
    private final EntityFinder entityFinder; // THÊM

    // Constructor đã được dọn dẹp
    public BookService(BookRepository bookRepository, EntityMapper entityMapper, EntityFinder entityFinder) {
        this.bookRepository = bookRepository;
        this.entityMapper = entityMapper;
        this.entityFinder = entityFinder;
    }

    // ------------------- READ Operations -------------------

    @Transactional(readOnly = true) // Thêm readOnly cho các hàm chỉ đọc
    public List<BookResponse> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(entityMapper::toBookResponse) // SỬA
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookResponse findBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sách với ID: " + id));
        return entityMapper.toBookResponse(book); // SỬA
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByCategoryName(String categoryName) {
        List<Book> books = bookRepository.findByCategoryNameContainingIgnoreCase(categoryName);
        return books.stream()
                .map(entityMapper::toBookResponse) // SỬA
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String title, String author, String categoryName) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
                title, author, categoryName);
        return books.stream()
                .map(entityMapper::toBookResponse) // SỬA
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByCategoryId(Long categoryId) {
        List<Book> books = bookRepository.findAllByCategoryId(categoryId);
        return books.stream()
                .map(entityMapper::toBookResponse) // SỬA
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        return books.stream()
                .map(entityMapper::toBookResponse) // SỬA
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByAuthor(String author) {
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);
        return books.stream()
                .map(entityMapper::toBookResponse) // SỬA
                .collect(Collectors.toList());
    }

    // ------------------- WRITE Operations -------------------

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        log.info("=== CREATE BOOK START ===");
        log.info("Request: {}", request);

        // Kiểm tra ISBN đã tồn tại
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            log.error("ISBN already exists: {}", request.getIsbn());
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Mã ISBN đã tồn tại.");
        }

        try {
            // Tìm category và publisher
            Category category = entityFinder.findCategoryById(request.getCategoryId());
            Publisher publisher = entityFinder.findPublisherById(request.getPublisherId());

            log.info("Found category: {} - {}", category.getId(), category.getName());
            log.info("Found publisher: {} - {}", publisher.getId(), publisher.getName());

            // Tạo entity
            Book book = entityMapper.toBookEntity(request, category, publisher);
            log.info("Book entity created: {}", book.getTitle());

            // Lưu book
            Book savedBook = bookRepository.save(book);
            log.info("Book saved with ID: {}", savedBook.getId());

            // Verify save
            Book verifiedBook = bookRepository.findById(savedBook.getId())
                    .orElseThrow(() -> new RuntimeException("Book not found after save!"));
            log.info("Book verified: {}", verifiedBook.getId());

            return entityMapper.toBookResponse(savedBook);

        } catch (Exception e) {
            log.error("Error creating book: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== CREATE BOOK END ===");
        }
    }

    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sách với ID: " + id));

        // 1. Ánh xạ các trường cơ bản
        entityMapper.updateBookFromRequest(book, request);

        // 2. Cập nhật Category (nếu có yêu cầu)
        if (request.getCategoryId() != null && !request.getCategoryId().equals(book.getCategory().getId())) {
            // GỌN GÀNG:
            book.setCategory(entityFinder.findCategoryById(request.getCategoryId()));
        }

        // 3. Cập nhật Publisher (nếu có yêu cầu)
        if (request.getPublisherId() != null && !request.getPublisherId().equals(book.getPublisher().getId())) {
            // GỌN GÀNG:
            book.setPublisher(entityFinder.findPublisherById(request.getPublisherId()));
        }

        // Lưu ý: Không cho phép cập nhật ISBN

        return entityMapper.toBookResponse(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sách với ID: " + id);
        }
        bookRepository.deleteById(id);
    }

}