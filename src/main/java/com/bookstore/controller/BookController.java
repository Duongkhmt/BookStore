package com.bookstore.controller;

import com.bookstore.dto.request.CreateBookRequest;
import com.bookstore.dto.request.UpdateBookRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = bookService.findAllBooks();
        ApiResponse<List<BookResponse>> response = new ApiResponse<>(true, "Lấy danh sách sách thành công", books);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.findBookById(id);
        ApiResponse<BookResponse> response = new ApiResponse<>(true, "Lấy thông tin sách thành công", book);
        return ResponseEntity.ok(response);
    }

    // Chỉ ADMIN mới có quyền thêm sách
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody CreateBookRequest request) {
        BookResponse newBook = bookService.createBook(request);
        ApiResponse<BookResponse> response = new ApiResponse<>(true, "Thêm sách thành công", newBook);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        BookResponse updatedBook = bookService.updateBook(id, request);
        ApiResponse<BookResponse> response = new ApiResponse<>(
                true, "Cập nhật sách thành công", updatedBook);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        ApiResponse<?> response = new ApiResponse<>(
                true, "Xóa sách thành công", null);
        return ResponseEntity.ok(response);
    }
}