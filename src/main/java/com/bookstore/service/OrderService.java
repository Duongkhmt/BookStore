
package com.bookstore.service;

import com.bookstore.dto.request.*;
import com.bookstore.dto.response.OrderResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.*;
import com.bookstore.service.helper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepo;
    private final BookRepository bookRepo;
    private final InventoryRepository invRepo;
    private final OrderTrackingRepository trackRepo;
    private final EntityFinder finder;
    private final RecordFactory factory;
    private final EntityMapper mapper;

    public OrderService(OrderRepository orderRepo, BookRepository bookRepo,
                        InventoryRepository invRepo, OrderTrackingRepository trackRepo,
                        EntityFinder finder, RecordFactory factory, EntityMapper mapper) {
        this.orderRepo = orderRepo;
        this.bookRepo = bookRepo;
        this.invRepo = invRepo;
        this.trackRepo = trackRepo;
        this.finder = finder;
        this.factory = factory;
        this.mapper = mapper;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest req, String username) {
        User user = finder.findUser(username);
        Order order = new Order(user, req.getShippingAddress(), OrderStatus.PENDING, LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        List<Inventory> pendingInventories = new ArrayList<>();

        // Lấy tất cả book IDs
        List<Long> bookIds = req.getItems().stream()
                .map(CartItemRequest::getBookId)
                .collect(Collectors.toList());

        Map<Long, Book> bookMap = bookRepo.findAllById(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, book -> book));

        for (CartItemRequest itemReq : req.getItems()) {
            Book book = bookMap.get(itemReq.getBookId());
            if (book == null) {
                book = finder.findBook(itemReq.getBookId());
            }

            if (book.getStockQuantity() < itemReq.getQuantity()) {
                throw new ApplicationException(ErrorCode.INSUFFICIENT_STOCK,
                        "Sách '" + book.getTitle() + "' không đủ tồn kho. Còn: " + book.getStockQuantity());
            }

            // Cập nhật số lượng tồn kho
            book.setStockQuantity(book.getStockQuantity() - itemReq.getQuantity());

            // Tạo inventory record
            pendingInventories.add(factory.createInventory(
                    book, itemReq.getQuantity(), InventoryType.OUT, "Xuất kho (Chờ Order ID)"
            ));

            // Tạo order item
            OrderItem orderItem = new OrderItem(order, book, itemReq.getQuantity(), book.getPrice());
            orderItems.add(orderItem);
        }

        // Lưu sách đã cập nhật
        bookRepo.saveAll(bookMap.values());

        // Thiết lập order items và tính tổng tiền
        order.setOrderItems(orderItems);
        order.calculateTotalAmount(); // 🚀 Dùng helper method

        // Lưu order
        Order savedOrder = orderRepo.save(order);

        // Cập nhật inventory records
        List<Inventory> finalInventories = pendingInventories.stream()
                .map(inv -> {
                    inv.setReason("Xuất kho cho Đơn hàng #" + savedOrder.getId());
                    return inv;
                })
                .collect(Collectors.toList());
        invRepo.saveAll(finalInventories);

        // Tạo tracking record
        savedOrder.addTracking(OrderStatus.PENDING, "Đơn hàng đã được tạo."); // 🚀 Dùng helper method
        Order updatedOrder = orderRepo.save(savedOrder);

        return mapper.toOrderResponse(updatedOrder);
    }

        @Transactional(readOnly = true)
        public List<OrderResponse> getMyOrders(String username) {
            log.info("Getting orders for user: {}", username);
            return orderRepo.findByUserUsernameOrderByOrderDateDesc(username)
                    .stream()
                    .map(mapper::toOrderResponse)
                    .collect(Collectors.toList());
        }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepo.findAll(pageable).map(order -> {
            OrderResponse res = mapper.toOrderResponse(order);

            // Tối ưu: Đếm số lượng để hiện ra bảng
            if (order.getOrderItems() != null) {
                res.setItemCount(order.getOrderItems().size());
            }

            // QUAN TRỌNG: Set items = null để giảm tải JSON
            res.setItems(null);
            return res;
        });
    }

        @Transactional
        public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest req) {
            Order order = finder.findOrderWithDetails(orderId);
            OrderStatus newStatus = OrderStatus.valueOf(req.getNewStatus().toUpperCase());

            // Xử lý hủy đơn hàng
            if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
                handleOrderCancellation(order, orderId);
            }

            // Cập nhật tracking và status
            order.addTracking(newStatus, req.getNote()); // 🚀 Dùng helper method
            order.setStatus(newStatus);

            Order updatedOrder = orderRepo.save(order);
            return mapper.toOrderResponse(updatedOrder);
        }

        @Transactional(readOnly = true)
        public OrderResponse getOrderDetails(Long orderId) {
            Order order = finder.findOrderWithDetails(orderId);
            return mapper.toOrderResponse(order);
        }

        private void handleOrderCancellation(Order order, Long orderId) {
            List<Book> booksToUpdate = new ArrayList<>();
            List<Inventory> inventoriesToCreate = new ArrayList<>();

            order.getOrderItems().forEach(orderItem -> {
                Book book = orderItem.getBook();
                book.setStockQuantity(book.getStockQuantity() + orderItem.getQuantity());
                booksToUpdate.add(book);

                inventoriesToCreate.add(factory.createInventory(
                        book, orderItem.getQuantity(), InventoryType.IN,
                        "Nhập kho do hủy đơn hàng #" + orderId
                ));
            });

            bookRepo.saveAll(booksToUpdate);
            invRepo.saveAll(inventoriesToCreate);
        }
}

