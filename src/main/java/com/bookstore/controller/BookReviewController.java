package com.bookstore.controller;

import com.bookstore.dto.request.ReviewRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.ReviewResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.BookReview;
import com.bookstore.entity.User;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.BookReviewRepository;
import com.bookstore.service.helper.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class BookReviewController {

    private final BookReviewRepository reviewRepository;
    private final EntityFinder entityFinder;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByBook(@PathVariable Long bookId) {
        List<BookReview> reviews = reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId);
        List<ReviewResponse> response = reviews.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy đánh giá thành công", response));
    }

    @PostMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long bookId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {

        Book book = entityFinder.findBook(bookId);

        // Kiểm tra xem đã review chưa
        reviewRepository.findByBookIdAndUserId(bookId, currentUser.getId())
                .ifPresent(r -> {
                    throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Bạn đã đánh giá sách này rồi");
                });

        BookReview review = new BookReview();
        review.setBook(book);
        review.setUser(currentUser);
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setCreatedAt(LocalDateTime.now());

        BookReview saved = reviewRepository.save(review);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đánh giá thành công", toResponse(saved)));
    }

    private ReviewResponse toResponse(BookReview review) {
        ReviewResponse res = new ReviewResponse();
        res.setId(review.getId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setCreatedAt(review.getCreatedAt());

        if (review.getUser() != null) {
            res.setUserId(review.getUser().getId());
            res.setUsername(review.getUser().getUsername());
        }
        return res;
    }
}
