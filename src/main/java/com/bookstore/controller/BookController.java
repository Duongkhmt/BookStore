package com.bookstore.controller;

import com.bookstore.dto.request.CreateBookRequest;
import com.bookstore.dto.request.UpdateBookRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.dto.response.BookSimpleResponse; // Import DTO này
import com.bookstore.entity.Book;                     // Import Entity
import com.bookstore.entity.DifficultyLevel;            // Import Enum
import com.bookstore.service.BookService;
import com.bookstore.service.helper.EntityFinder;       // Import EntityFinder
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final EntityFinder entityFinder; // Cần thêm cái này để tìm Book Entity gốc

    // Inject cả BookService và EntityFinder vào Constructor
    public BookController(BookService bookService, EntityFinder entityFinder) {
        this.bookService = bookService;
        this.entityFinder = entityFinder;
    }

    // --- 1. API CƠ BẢN (GIỮ NGUYÊN) ---

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = bookService.findAllBooks();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách sách thành công", books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.findBookById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin sách thành công", book));
    }

    // --- 2. API MỚI: TÌM KIẾM & LỌC ---

    // Tìm kiếm chung (Title, Author, Category)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchBooks(
            @RequestParam(required = false, defaultValue = "") String keyword) {

        // Gọi hàm searchBooks trong Service (tìm theo title, author HOẶC category)
        List<BookResponse> books = bookService.searchBooks(keyword, keyword, keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Kết quả tìm kiếm", books));
    }

    // Lọc nâng cao (Level, Tag, Topic) - Dùng cho Sidebar Filter
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<BookResponse>>> filterBooks(
            @RequestParam(required = false) DifficultyLevel level,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Long topicId) {

        // Gọi hàm filterBooks (bạn cần đảm bảo đã thêm hàm này vào Service như hướng dẫn trước)
        List<Book> books = bookService.filterBooks(level, tagId, topicId);

        // Convert Entity -> Response DTO
        List<BookResponse> response = books.stream()
                .map(bookService::toDetailedResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Kết quả lọc sách", response));
    }

    // --- 3. API MỚI: GỢI Ý SÁCH (QUAN TRỌNG) ---

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<ApiResponse<List<BookSimpleResponse>>> getRecommendations(@PathVariable Long id) {
        // 1. Tìm entity sách gốc
        Book book = entityFinder.findBook(id);

        // 2. Gọi thuật toán gợi ý trong Service
        List<BookSimpleResponse> recommendations = bookService.getRecommendedBooks(book, 10);

        return ResponseEntity.ok(new ApiResponse<>(true, "Sách gợi ý", recommendations));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getFeaturedBooks() {
        List<BookResponse> books = bookService.findFeaturedBooks();
        return ResponseEntity.ok(new ApiResponse<>(true, "Danh sách sách nổi bật (Top Rated)", books));
    }

    // --- 4. API ADMIN (CRUD - GIỮ NGUYÊN) ---

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody CreateBookRequest request) {
        BookResponse newBook = bookService.createBook(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Thêm sách thành công", newBook), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        BookResponse updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật sách thành công", updatedBook));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa sách thành công", null));
    }
}