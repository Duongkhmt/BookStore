package com.bookstore.service.helper;

import com.bookstore.dto.request.CreateBookRequest;
import com.bookstore.dto.request.UpdateBookRequest;
import com.bookstore.dto.response.*;
import com.bookstore.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
@Slf4j
public class EntityMapper {

    public PaymentResponse toPaymentResponse(Payment payment) {
        if (payment == null) return null;

        PaymentResponse res = new PaymentResponse();
        res.setId(payment.getId());
        res.setOrderId(payment.getOrder() != null ? payment.getOrder().getId() : null);
        res.setPaymentMethod(payment.getPaymentMethod());
        res.setAmount(payment.getAmount());
        res.setTransactionId(payment.getTransactionId());
        res.setPaymentDate(payment.getPaymentDate());
        res.setStatus(payment.getStatus());
        return res;
    }

    public OrderResponse toOrderResponse(Order order) {
        log.debug("Mapping order to response: {}", order.getId());

        OrderResponse res = new OrderResponse();
        res.setId(order.getId());

        // 🚨 SỬA: Dùng getter mới thay vì order.getUser().getUsername()
        res.setUsername(order.getUsername()); // Sử dụng getter mới từ Order entity
        res.setShippingAddress(order.getShippingAddress());
        res.setTotalAmount(order.getTotalAmount());
        res.setOrderDate(order.getOrderDate());
        res.setStatus(order.getStatus().name());


        // Payment - xử lý cẩn thận
        if (order.getPayment() != null) {
            try {
                res.setPayment(toPaymentResponse(order.getPayment()));
            } catch (Exception e) {
                log.warn("Error mapping payment for order {}: {}", order.getId(), e.getMessage());
                res.setPayment(null);
            }
        }

        // --- SỬA ĐOẠN NÀY ---
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            // 1. Luôn đếm số lượng để hiển thị ra bảng (Table)
            res.setItemCount(order.getOrderItems().size());

            // 2. Map chi tiết (Service sẽ quyết định có giữ lại list này hay xóa đi tùy API)
            try {
                res.setItems(order.getOrderItems().stream()
                        .map(this::toOrderItemResponse)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                log.warn("Error mapping items: {}", e.getMessage());
                res.setItems(new ArrayList<>());
            }
        } else {
            res.setItemCount(0); // Không có item nào
            res.setItems(new ArrayList<>());
        }

        // Tracking History - xử lý cẩn thận
        if (order.getTrackingHistory() != null && !order.getTrackingHistory().isEmpty()) {
            try {
                res.setTrackingHistory(order.getTrackingHistory().stream()
                        .map(this::toOrderTrackingResponse)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                log.warn("Error mapping tracking history for order {}: {}", order.getId(), e.getMessage());
                res.setTrackingHistory(new ArrayList<>());
            }
        } else {
            res.setTrackingHistory(new ArrayList<>());
        }

        log.debug("Successfully mapped order: {}", order.getId());
        return res;
    }

    public InventoryResponse toInventoryResponse(Inventory inv) {
        InventoryResponse res = new InventoryResponse();
        res.setId(inv.getId());
        res.setBookId(inv.getBook().getId());
        res.setBookTitle(inv.getBook().getTitle());
        res.setQuantityChange(inv.getQuantityChange());
        res.setType(inv.getType());
        res.setReason(inv.getReason());
        res.setTimestamp(inv.getTimestamp());
        return res;
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        if (item == null) return null;

        OrderItemResponse res = new OrderItemResponse();
        res.setId(item.getId());

        if (item.getBook() != null) {
            res.setBookId(item.getBook().getId());
            res.setBookTitle(item.getBook().getTitle());
        }

        res.setQuantity(item.getQuantity());

        // ✅ MAP DỮ LIỆU:
        res.setPriceAtOrder(item.getPriceAtOrder());

        // ✅ TÍNH SUBTOTAL (Quan trọng):
        if (item.getPriceAtOrder() != null) {
            res.setSubtotal(item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            res.setSubtotal(BigDecimal.ZERO);
        }

        return res;
    }

    public OrderTrackingResponse toOrderTrackingResponse(OrderTracking track) {
        OrderTrackingResponse res = new OrderTrackingResponse();
        res.setId(track.getId());
        res.setOrderId(track.getOrder().getId());
        res.setStatus(track.getStatus());
        res.setTimestamp(track.getTimestamp());
        res.setNote(track.getNote());
        return res;
    }


    public ReceiptResponse toReceiptResponse(Receipt receipt) {
        if (receipt == null) return null;

        ReceiptResponse response = new ReceiptResponse();
        response.setId(receipt.getId());
        response.setReceiptCode(receipt.getReceiptCode());
        response.setReceiptDate(receipt.getReceiptDate());
        response.setNote(receipt.getNote());
        response.setTotalAmount(receipt.getTotalAmount());

        // 1. Map thông tin Supplier (Tên & ID)
        if (receipt.getSupplier() != null) {
            response.setSupplier(receipt.getSupplier().getName());
            response.setSupplierId(receipt.getSupplier().getId()); // Thêm dòng này
        }

        // 2. Map người tạo
        if (receipt.getCreatedBy() != null) {
            response.setCreatedByUsername(receipt.getCreatedBy().getUsername());
        }

        // 3. Xử lý Items (QUAN TRỌNG)
        if (receipt.getItems() != null && !receipt.getItems().isEmpty()) {
            // Luôn set số lượng để hiển thị ra bảng
            response.setItemCount(receipt.getItems().size());

            // Map chi tiết sang List (Lưu ý: Dùng Collectors.toList())
            List<ReceiptItemResponse> itemResponses = receipt.getItems().stream()
                    .map(this::toReceiptItemResponse)
                    .collect(Collectors.toList());

            response.setItems(itemResponses);
        } else {
            response.setItemCount(0);
            response.setItems(new ArrayList<>());
        }

        return response;
    }

    private ReceiptItemResponse toReceiptItemResponse(ReceiptItem item) {
        ReceiptItemResponse response = new ReceiptItemResponse();
        response.setId(item.getId());
        response.setBookId(item.getBook().getId());
        response.setBookTitle(item.getBook().getTitle());
        response.setQuantity(item.getQuantity());
        response.setImportPrice(item.getImportPrice());
        response.setSubtotal(item.getSubtotal());
        return response;
    }

    // ===== CÁC PHƯƠNG THỨC MAP CỦA BOOK (MỚI THÊM VÀO) =====
    // ==========================================================

    public BookResponse toBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setPrice(book.getPrice());
        response.setStockQuantity(book.getStockQuantity());
        response.setDifficultyLevel(book.getDifficultyLevel());
        response.setSummary(book.getSummary());
        response.setPublicationYear(book.getPublicationYear());
        response.setPageCount(book.getPageCount());

        if (book.getCategory() != null) {
            response.setCategoryId(book.getCategory().getId());
            response.setCategoryName(book.getCategory().getName());
        }
        if (book.getPublisher() != null) {
            response.setPublisherId(book.getPublisher().getId());
            response.setPublisherName(book.getPublisher().getName());
        }

        // Tags
        response.setTags(book.getTags().stream()
                .map(tag -> {
                    TagResponse tr = new TagResponse();
                    tr.setId(tag.getId());
                    tr.setName(tag.getName());
                    tr.setTagType(tag.getTagType());
                    tr.setDescription(tag.getDescription());
                    return tr;
                }).toList());

        // Topics
        response.setTopics(book.getTopics().stream()
                .map(topic -> {
                    TopicResponse tr = new TopicResponse();
                    tr.setId(topic.getId());
                    tr.setName(topic.getName());
                    tr.setDescription(topic.getDescription());
                    return tr;
                }).toList());

        return response;
    }

    /**
     * Tạo một Book entity mới từ CreateBookRequest.
     */
    public Book toBookEntity(CreateBookRequest request, Category category, Publisher publisher) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setStockQuantity(request.getStockQuantity());
        book.setDifficultyLevel(request.getDifficultyLevel());
        book.setSummary(request.getSummary());
        book.setPublicationYear(request.getPublicationYear());
        book.setPageCount(request.getPageCount());
        book.setCategory(category);
        book.setPublisher(publisher);
        return book;
    }

    /**
     * Cập nhật một Book entity (đã tồn tại) từ UpdateBookRequest.
     */
    public void updateBookFromRequest(Book book, UpdateBookRequest request) {
        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getAuthor() != null) book.setAuthor(request.getAuthor());
        if (request.getPrice() != null) book.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) book.setStockQuantity(request.getStockQuantity());
        if (request.getDifficultyLevel() != null) book.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getSummary() != null) book.setSummary(request.getSummary());
        if (request.getPublicationYear() != null) book.setPublicationYear(request.getPublicationYear());
        if (request.getPageCount() != null) book.setPageCount(request.getPageCount());
    }

    // ==========================================================
    // ===== CÁC PHƯƠC THỨC MAP CỦA CATEGORY (MỚI THÊM VÀO) =====
    // ==========================================================

    public CategoryResponse toCategoryResponse(Category category) {
        if (category == null) return null;

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        return response;
    }

    public Category toCategoryEntity(String trimmedName, String description) {
        Category category = new Category();
        category.setName(trimmedName);
        category.setDescription(description);
        return category;
    }

    public SupplierResponse toSupplierResponse(Supplier supplier) {
        if (supplier == null) return null;

        SupplierResponse response = new SupplierResponse();
        response.setId(supplier.getId());
        response.setName(supplier.getName());
        response.setContactPerson(supplier.getContactPerson());
        response.setPhone(supplier.getPhone());
        response.setEmail(supplier.getEmail());
        response.setAddress(supplier.getAddress());
        response.setDescription(supplier.getDescription());
        response.setIsActive(supplier.getIsActive());
        response.setCreatedAt(supplier.getCreatedAt());
        response.setUpdatedAt(supplier.getUpdatedAt());

        return response;
    }
}
