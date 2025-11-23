package com.bookstore.service;

import com.bookstore.dto.request.CreateInventoryRequest;
import com.bookstore.dto.response.InventoryResponse;
import com.bookstore.entity.*;
import com.bookstore.repository.*;
import com.bookstore.service.helper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final InventoryRepository invRepo;
    private final BookRepository bookRepo;
    private final EntityFinder finder;
    private final EntityMapper mapper;

    public InventoryService(InventoryRepository invRepo, BookRepository bookRepo,
                            EntityFinder finder, EntityMapper mapper) {
        this.invRepo = invRepo;
        this.bookRepo = bookRepo;
        this.finder = finder;
        this.mapper = mapper;
    }

    @Transactional
    public InventoryResponse createInventoryRecord(CreateInventoryRequest req) {
        Book book = finder.findBook(req.getBookId());
        InventoryType type = req.getType();

        if (req.getQuantityChange() <= 0)
            throw new IllegalArgumentException("Số lượng thay đổi phải > 0");

        if (type == InventoryType.IN) book.setStockQuantity(book.getStockQuantity() + req.getQuantityChange());
        else if (book.getStockQuantity() < req.getQuantityChange())
            throw new IllegalArgumentException("Không đủ tồn kho để xuất thủ công.");
        else book.setStockQuantity(book.getStockQuantity() - req.getQuantityChange());

        bookRepo.save(book);

        Inventory inv = new Inventory(book, req.getQuantityChange(), type, req.getReason(), LocalDateTime.now());
        return mapper.toInventoryResponse(invRepo.save(inv));
    }

    public List<InventoryResponse> getInventoryHistoryByBook(Long bookId) {
        return invRepo.findByBookIdOrderByTimestampDesc(bookId).stream()
                .map(mapper::toInventoryResponse)
                .collect(Collectors.toList());
    }
    // Thêm các method này vào InventoryService
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventoryRecords() {
        return invRepo.findAllByOrderByTimestampDesc().stream()
                .map(mapper::toInventoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryRecordById(Long id) {
        Inventory inventory = invRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi tồn kho với ID: " + id));
        return mapper.toInventoryResponse(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryRecordsByType(InventoryType type) {
        return invRepo.findByTypeOrderByTimestampDesc(type).stream()
                .map(mapper::toInventoryResponse)
                .collect(Collectors.toList());
    }
}
