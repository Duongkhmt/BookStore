//package com.bookstore.service;
//
//import com.bookstore.dto.request.CreateBookRequest;
//import com.bookstore.dto.request.UpdateBookRequest;
//import com.bookstore.dto.response.BookResponse;
//import com.bookstore.dto.response.BookSimpleResponse;
//import com.bookstore.entity.*;
//import com.bookstore.exception.ApplicationException;
//import com.bookstore.exception.ErrorCode;
//import com.bookstore.repository.BookRepository;
//import com.bookstore.repository.BookReviewRepository;
//import com.bookstore.repository.TagRepository;
//import com.bookstore.repository.TopicRepository;
//import com.bookstore.service.helper.EntityFinder;
//import com.bookstore.service.helper.EntityMapper;
//import jakarta.persistence.criteria.Join; // IMPORT MỚI
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.jpa.domain.Specification; // IMPORT MỚI
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import org.springframework.data.domain.PageRequest;
//
//
//import java.awt.print.Pageable;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class BookService {
//
//    private final BookRepository bookRepository;
//    private final EntityMapper entityMapper;
//    private final EntityFinder entityFinder;
//    private final TagRepository tagRepository;
//    private final TopicRepository topicRepository;
//    private final BookReviewRepository reviewRepository;
//
//    // ------------------- READ Operations -------------------
//
//    @Transactional(readOnly = true)
//    public List<BookResponse> findAllBooks() {
//        return bookRepository.findAll().stream()
//                .map(this::toDetailedResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public BookResponse findBookById(Long id) {
//        Book book = entityFinder.findBook(id);
//        return toDetailedResponse(book);
//    }
//
//    @Transactional(readOnly = true)
//    public List<BookResponse> findBooksByCategoryName(String categoryName) {
//        return bookRepository.findByCategoryNameContainingIgnoreCase(categoryName).stream()
//                .map(entityMapper::toBookResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<BookResponse> searchBooks(String title, String author, String categoryName) {
//        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
//                        title, author, categoryName).stream()
//                .map(entityMapper::toBookResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<BookResponse> findBooksByCategoryId(Long categoryId) {
//        return bookRepository.findAllByCategoryId(categoryId).stream()
//                .map(entityMapper::toBookResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<BookResponse> findBooksByTitle(String title) {
//        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
//                .map(entityMapper::toBookResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<BookResponse> findBooksByAuthor(String author) {
//        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
//                .map(entityMapper::toBookResponse)
//                .collect(Collectors.toList());
//    }
//
//    // --- MỚI: HÀM LỌC NÂNG CAO (CHO CONTROLLER GỌI) ---
//    // Yêu cầu: BookRepository phải extends JpaSpecificationExecutor<Book>
//    @Transactional(readOnly = true)
//    public List<Book> filterBooks(DifficultyLevel level, Long tagId, Long topicId) {
//        Specification<Book> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
//
//        // 1. Lọc theo Level
//        if (level != null) {
//            spec = spec.and((root, query, cb) -> cb.equal(root.get("difficultyLevel"), level));
//        }
//
//        // 2. Lọc theo Tag (Join bảng tags)
//        if (tagId != null) {
//            spec = spec.and((root, query, cb) -> {
//                Join<Book, Tag> tags = root.join("tags");
//                return cb.equal(tags.get("id"), tagId);
//            });
//        }
//
//        // 3. Lọc theo Topic (Join bảng topics)
//        if (topicId != null) {
//            spec = spec.and((root, query, cb) -> {
//                Join<Book, Topic> topics = root.join("topics");
//                return cb.equal(topics.get("id"), topicId);
//            });
//        }
//
//        return bookRepository.findAll(spec);
//    }
//
//    // ------------------- WRITE Operations -------------------
//
//    @Transactional
//    public BookResponse createBook(CreateBookRequest request) {
//        if (bookRepository.existsByIsbn(request.getIsbn())) {
//            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Mã ISBN đã tồn tại.");
//        }
//
//        Category category = entityFinder.findCategoryById(request.getCategoryId());
//        Publisher publisher = entityFinder.findPublisherById(request.getPublisherId());
//
//        Book book = entityMapper.toBookEntity(request, category, publisher);
//
//        // Xử lý Tags
//        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
//            Set<Tag> tags = request.getTagIds().stream()
//                    .map(entityFinder::findTagById)
//                    .collect(Collectors.toSet());
//            book.setTags(tags);
//        }
//
//        // Xử lý Topics
//        if (request.getTopicIds() != null && !request.getTopicIds().isEmpty()) {
//            Set<Topic> topics = request.getTopicIds().stream()
//                    .map(entityFinder::findTopicById)
//                    .collect(Collectors.toSet());
//            book.setTopics(topics);
//        }
//
//        Book saved = bookRepository.save(book);
//        return toDetailedResponse(saved);
//    }
//
//    @Transactional
//    public BookResponse updateBook(Long id, UpdateBookRequest request) {
//        Book book = entityFinder.findBook(id);
//
//        entityMapper.updateBookFromRequest(book, request);
//
//        if (request.getCategoryId() != null) {
//            book.setCategory(entityFinder.findCategoryById(request.getCategoryId()));
//        }
//        if (request.getPublisherId() != null) {
//            book.setPublisher(entityFinder.findPublisherById(request.getPublisherId()));
//        }
//
//        if (request.getTagIds() != null) {
//            book.getTags().clear();
//            Set<Tag> newTags = request.getTagIds().stream()
//                    .map(entityFinder::findTagById)
//                    .collect(Collectors.toSet());
//            book.getTags().addAll(newTags);
//        }
//
//        if (request.getTopicIds() != null) {
//            book.getTopics().clear();
//            Set<Topic> newTopics = request.getTopicIds().stream()
//                    .map(entityFinder::findTopicById)
//                    .collect(Collectors.toSet());
//            book.getTopics().addAll(newTopics);
//        }
//
//        return toDetailedResponse(bookRepository.save(book));
//    }
//
//    @Transactional
//    public void deleteBook(Long id) {
//        if (!bookRepository.existsById(id)) {
//            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sách với ID: " + id);
//        }
//        bookRepository.deleteById(id);
//    }
//
//    // ------------------- HELPER / LOGIC GỢI Ý -------------------
//
//    public BookResponse toDetailedResponse(Book book) {
//        BookResponse resp = entityMapper.toBookResponse(book);
//
//        // Rating: Handle Null Safe
//        Double avgRating = reviewRepository.findAverageRatingByBookId(book.getId()).orElse(0.0);
//        resp.setAverageRating(Math.round(avgRating * 10.0) / 10.0);
//
//        Long totalReviews = reviewRepository.countByBookId(book.getId());
//        resp.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);
//
//        // Gợi ý sách
//        resp.setRelatedBooks(getRecommendedBooks(book, 6));
//
//        return resp;
//    }
//
//    // ĐÃ SỬA: private -> public để Controller gọi được
//    public List<BookSimpleResponse> getRecommendedBooks(Book source, int limit) {
//        Set<Book> candidates = new HashSet<>();
//
//        // 1. Tìm theo Level
//        if (source.getDifficultyLevel() != null) {
//            candidates.addAll(bookRepository.findByDifficultyLevel(source.getDifficultyLevel()));
//        }
//
//        // 2. Tìm theo Tags
//        if (source.getTags() != null) {
//            source.getTags().forEach(tag ->
//                    tagRepository.findById(tag.getId()).ifPresent(t -> {
//                        if (t.getBooks() != null) candidates.addAll(t.getBooks());
//                    })
//            );
//        }
//
//        // 3. Tìm theo Topics
//        if (source.getTopics() != null) {
//            source.getTopics().forEach(topic ->
//                    topicRepository.findById(topic.getId()).ifPresent(t -> {
//                        if (t.getBooks() != null) candidates.addAll(t.getBooks());
//                    })
//            );
//        }
//
//        // 4. Lọc, tính điểm và trả về
//        return candidates.stream()
//                .filter(b -> !b.getId().equals(source.getId())) // Loại bỏ chính nó
//                .sorted((b1, b2) -> Integer.compare(
//                        calculateSimilarity(source, b2), // Sắp xếp giảm dần theo độ giống
//                        calculateSimilarity(source, b1)
//                ))
//                .limit(limit)
//                .map(this::toSimpleResponse)
//                .collect(Collectors.toList());
//    }
//
//    private int calculateSimilarity(Book source, Book target) {
//        int score = 0;
//
//        // Cùng Level: +5 điểm
//        if (source.getDifficultyLevel() != null && source.getDifficultyLevel() == target.getDifficultyLevel()) {
//            score += 5;
//        }
//
//        // Cùng Tags: +3 điểm/tag
//        if (source.getTags() != null && target.getTags() != null) {
//            long commonTags = source.getTags().stream()
//                    .filter(t -> target.getTags().contains(t))
//                    .count();
//            score += (int) (commonTags * 3);
//        }
//
//        // Cùng Topics: +2 điểm/topic
//        if (source.getTopics() != null && target.getTopics() != null) {
//            long commonTopics = source.getTopics().stream()
//                    .filter(t -> target.getTopics().contains(t))
//                    .count();
//            score += (int) (commonTopics * 2);
//        }
//
//        return score;
//    }
//
//    private BookSimpleResponse toSimpleResponse(Book book) {
//        BookSimpleResponse resp = new BookSimpleResponse();
//        resp.setId(book.getId());
//        resp.setTitle(book.getTitle());
//        resp.setAuthor(book.getAuthor());
//        resp.setIsbn(book.getIsbn());
//        resp.setPrice(book.getPrice());
//        resp.setDifficultyLevel(book.getDifficultyLevel() != null ? book.getDifficultyLevel().name() : null);
//        return resp;
//    }
//
//    @Transactional(readOnly = true)
//    public List<BookResponse> findFeaturedBooks() {
//        return bookRepository.findFeaturedBooks()
//                .stream()
//                .map(this::toDetailedResponse)
//                .collect(Collectors.toList());
//    }
//
//}

package com.bookstore.service;

import com.bookstore.dto.request.CreateBookRequest;
import com.bookstore.dto.request.UpdateBookRequest;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.dto.response.BookSimpleResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.BookReviewRepository;
import com.bookstore.repository.TagRepository;
import com.bookstore.repository.TopicRepository;
import com.bookstore.service.helper.EntityFinder;
import com.bookstore.service.helper.EntityMapper;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final EntityMapper entityMapper;
    private final EntityFinder entityFinder;
    private final TagRepository tagRepository;
    private final TopicRepository topicRepository;
    private final BookReviewRepository reviewRepository;

    // ========================================================================
    // 1. CÁC HÀM ĐỌC DỮ LIỆU (READ) - ĐÃ TỐI ƯU PHÂN TRANG
    // ========================================================================

    /**
     * Lấy tất cả sách có phân trang
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> findAllBooks(int page, int size) {
        // Tạo đối tượng phân trang, sắp xếp theo ID giảm dần (sách mới nhất lên đầu)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return bookRepository.findAll(pageable)
                .map(this::toDetailedResponse);
    }

    /**
     * Xem chi tiết 1 cuốn sách
     */
    @Transactional(readOnly = true)
    public BookResponse findBookById(Long id) {
        Book book = entityFinder.findBook(id);
        return toDetailedResponse(book);
    }

    /**
     * Tìm sách theo tên Danh mục (Category) có phân trang
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> findBooksByCategoryName(String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByCategoryNameContainingIgnoreCase(categoryName, pageable)
                .map(entityMapper::toBookResponse);
    }

    /**
     * Tìm kiếm chung (Title, Author, Category) có phân trang
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String title, String author, String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
                        title, author, categoryName, pageable)
                .map(entityMapper::toBookResponse);
    }

    /**
     * Lấy sách theo ID Danh mục có phân trang
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> findBooksByCategoryId(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAllByCategoryId(categoryId, pageable)
                .map(entityMapper::toBookResponse);
    }

    /**
     * Tìm theo Title có phân trang
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> findBooksByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(entityMapper::toBookResponse);
    }

    /**
     * Tìm theo Author có phân trang
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> findBooksByAuthor(String author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable)
                .map(entityMapper::toBookResponse);
    }

    /**
     * Lọc nâng cao (Filter) kết hợp nhiều điều kiện + Phân trang
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> filterBooks(DifficultyLevel level, Long tagId, Long topicId, int page, int size) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        // 1. Lọc theo Level
        if (level != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("difficultyLevel"), level));
        }

        // 2. Lọc theo Tag (Join bảng tags)
        if (tagId != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Tag> tags = root.join("tags");
                return cb.equal(tags.get("id"), tagId);
            });
        }

        // 3. Lọc theo Topic (Join bảng topics)
        if (topicId != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Topic> topics = root.join("topics");
                return cb.equal(topics.get("id"), topicId);
            });
        }

        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(spec, pageable).map(this::toDetailedResponse);
    }

    /**
     * Lấy sách nổi bật (Có Cache để giảm tải DB)
     * Cache sẽ hết hạn sau 1 khoảng thời gian (cần cấu hình thêm ttl nếu muốn)
     */
    @Cacheable(value = "featuredBooks", key = "'home_featured'")
    @Transactional(readOnly = true)
    public List<BookResponse> findFeaturedBooks() {
        // Query native SQL đã limit 8 ở Repository
        return bookRepository.findFeaturedBooks()
                .stream()
                .map(this::toDetailedResponse)
                .collect(Collectors.toList());
    }

    // ========================================================================
    // 2. CÁC HÀM GHI DỮ LIỆU (WRITE) - Create, Update, Delete
    // ========================================================================

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Mã ISBN đã tồn tại.");
        }

        Category category = entityFinder.findCategoryById(request.getCategoryId());
        Publisher publisher = entityFinder.findPublisherById(request.getPublisherId());

        Book book = entityMapper.toBookEntity(request, category, publisher);

        // Xử lý Tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = request.getTagIds().stream()
                    .map(entityFinder::findTagById)
                    .collect(Collectors.toSet());
            book.setTags(tags);
        }

        // Xử lý Topics
        if (request.getTopicIds() != null && !request.getTopicIds().isEmpty()) {
            Set<Topic> topics = request.getTopicIds().stream()
                    .map(entityFinder::findTopicById)
                    .collect(Collectors.toSet());
            book.setTopics(topics);
        }

        Book saved = bookRepository.save(book);
        return toDetailedResponse(saved);
    }

    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        Book book = entityFinder.findBook(id);

        entityMapper.updateBookFromRequest(book, request);

        if (request.getCategoryId() != null) {
            book.setCategory(entityFinder.findCategoryById(request.getCategoryId()));
        }
        if (request.getPublisherId() != null) {
            book.setPublisher(entityFinder.findPublisherById(request.getPublisherId()));
        }

        if (request.getTagIds() != null) {
            book.getTags().clear();
            Set<Tag> newTags = request.getTagIds().stream()
                    .map(entityFinder::findTagById)
                    .collect(Collectors.toSet());
            book.getTags().addAll(newTags);
        }

        if (request.getTopicIds() != null) {
            book.getTopics().clear();
            Set<Topic> newTopics = request.getTopicIds().stream()
                    .map(entityFinder::findTopicById)
                    .collect(Collectors.toSet());
            book.getTopics().addAll(newTopics);
        }

        return toDetailedResponse(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sách với ID: " + id);
        }
        bookRepository.deleteById(id);
    }

    // ========================================================================
    // 3. HELPER METHODS & LOGIC GỢI Ý
    // ========================================================================

    /**
     * Chuyển đổi Entity sang Response, tính toán thêm Rating và Review Count
     */
    public BookResponse toDetailedResponse(Book book) {
        BookResponse resp = entityMapper.toBookResponse(book);

        // Lưu ý: Nếu dữ liệu quá lớn, việc gọi query này trong vòng lặp (N+1 query) sẽ chậm.
        // Tốt nhất là nên lưu averageRating vào bảng Book luôn.
        // Nhưng ở đây ta đã dùng Phân trang (size=10, 20) nên tạm chấp nhận được.
        Double avgRating = reviewRepository.findAverageRatingByBookId(book.getId()).orElse(0.0);
        resp.setAverageRating(Math.round(avgRating * 10.0) / 10.0);

        Long totalReviews = reviewRepository.countByBookId(book.getId());
        resp.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);

        // Lưu ý: Không nên gọi getRecommendedBooks() ở đây nếu đang load danh sách (findAll)
        // Chỉ nên gọi khi user xem chi tiết 1 cuốn (findBookById).
        // Tôi để tạm null hoặc list rỗng cho list view để tối ưu tốc độ.
        resp.setRelatedBooks(new ArrayList<>());

        return resp;
    }

    @Transactional(readOnly = true)
    public List<BookSimpleResponse> getRecommendedBooks(Book source, int limit) {
        Set<Book> candidates = new HashSet<>();

        // 1. Gợi ý theo cùng Level (Dùng Repository Query)
        if (source.getDifficultyLevel() != null) {
            candidates.addAll(bookRepository.findTop10ByDifficultyLevelAndIdNot(source.getDifficultyLevel(), source.getId()));
        }

        // 2. Gợi ý theo Tags (Lấy ID trước, sau đó query JOIN trong DB)
        if (source.getTags() != null && !source.getTags().isEmpty()) {
            Set<Long> tagIds = source.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
            candidates.addAll(bookRepository.findRelatedByTagIds(tagIds, source.getId()));
        }

        // 3. Gợi ý theo Topics
        if (source.getTopics() != null && !source.getTopics().isEmpty()) {
            Set<Long> topicIds = source.getTopics().stream().map(Topic::getId).collect(Collectors.toSet());
            candidates.addAll(bookRepository.findRelatedByTopicIds(topicIds, source.getId()));
        }

        // 4. Tính toán Similarity Score và trả về
        return candidates.stream()
                .filter(b -> !b.getId().equals(source.getId())) // Chắc chắn không trùng cuốn hiện tại
                .sorted((b1, b2) -> Integer.compare(
                        calculateSimilarity(source, b2), // Sách nào giống hơn lên đầu
                        calculateSimilarity(source, b1)
                ))
                .limit(limit)
                .map(this::toSimpleResponse)
                .collect(Collectors.toList());
    }

    private int calculateSimilarity(Book source, Book target) {
        int score = 0;

        // Cùng Level: +5 điểm
        if (source.getDifficultyLevel() != null && source.getDifficultyLevel() == target.getDifficultyLevel()) {
            score += 5;
        }

        // Cùng Tags: +3 điểm/tag
        if (source.getTags() != null && target.getTags() != null) {
            long commonTags = source.getTags().stream()
                    .filter(t -> target.getTags().contains(t))
                    .count();
            score += (int) (commonTags * 3);
        }

        // Cùng Topics: +2 điểm/topic
        if (source.getTopics() != null && target.getTopics() != null) {
            long commonTopics = source.getTopics().stream()
                    .filter(t -> target.getTopics().contains(t))
                    .count();
            score += (int) (commonTopics * 2);
        }

        return score;
    }

    private BookSimpleResponse toSimpleResponse(Book book) {
        BookSimpleResponse resp = new BookSimpleResponse();
        resp.setId(book.getId());
        resp.setTitle(book.getTitle());
        resp.setAuthor(book.getAuthor());
        resp.setIsbn(book.getIsbn());
        resp.setPrice(book.getPrice());
        resp.setDifficultyLevel(book.getDifficultyLevel() != null ? book.getDifficultyLevel().name() : null);
        return resp;
    }
}