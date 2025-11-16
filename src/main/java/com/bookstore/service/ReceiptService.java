package com.bookstore.service;

import com.bookstore.dto.request.CreateReceiptRequest;
import com.bookstore.dto.request.ReceiptItemRequest;
import com.bookstore.dto.response.ReceiptResponse;
import com.bookstore.entity.*;
import com.bookstore.repository.*;
import com.bookstore.service.helper.EntityFinder;
import com.bookstore.service.helper.EntityMapper;
import com.bookstore.service.helper.RecordFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReceiptService {

    private final ReceiptRepository receiptRepo;
    private final BookRepository bookRepo;
    private final InventoryRepository invRepo;
    private final EntityFinder finder;
    private final RecordFactory factory;
    private final EntityMapper mapper;

    public ReceiptService(ReceiptRepository receiptRepo, BookRepository bookRepo,
                          InventoryRepository invRepo, EntityFinder finder,
                          RecordFactory factory, EntityMapper mapper) {
        this.receiptRepo = receiptRepo;
        this.bookRepo = bookRepo;
        this.invRepo = invRepo;
        this.finder = finder;
        this.factory = factory;
        this.mapper = mapper;
    }

    @Transactional
    public ReceiptResponse createReceipt(CreateReceiptRequest req, String username) {
        try {
            log.info("Creating receipt for user: {}", username);

            User user = finder.findUser(username);
            Supplier supplier = finder.findSupplierById(req.getSupplierId()); // Tìm supplier theo ID

            Receipt receipt = new Receipt();
            receipt.setReceiptCode(generateReceiptCode());
            receipt.setCreatedBy(user);
            receipt.setReceiptDate(LocalDateTime.now());
            receipt.setSupplier(supplier); // Set supplier entity
            receipt.setNote(req.getNote());

            BigDecimal total = BigDecimal.ZERO;

            for (ReceiptItemRequest itemReq : req.getItems()) {
                Book book = finder.findBook(itemReq.getBookId());

                ReceiptItem item = new ReceiptItem();
                item.setReceipt(receipt);
                item.setBook(book);
                item.setQuantity(itemReq.getQuantity());
                item.setImportPrice(itemReq.getImportPrice());
                item.setSubtotal(itemReq.getImportPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

                receipt.getItems().add(item);
                total = total.add(item.getSubtotal());

                // Cập nhật tồn kho
                book.setStockQuantity(book.getStockQuantity() + itemReq.getQuantity());
                bookRepo.save(book);

                // Ghi vào lịch sử inventory
                invRepo.save(factory.createInventory(book, itemReq.getQuantity(), InventoryType.IN,
                        "Nhập kho - Phiếu " + receipt.getReceiptCode()));
            }

            receipt.setTotalAmount(total);
            Receipt saved = receiptRepo.save(receipt);
            log.info("Receipt created successfully with ID: {}", saved.getId());

            return mapper.toReceiptResponse(saved);

        } catch (Exception e) {
            log.error("Error creating receipt: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String generateReceiptCode() {
        return "RC" + System.currentTimeMillis();
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> getAllReceipts() {
        return receiptRepo.findAllByOrderByReceiptDateDesc().stream()
                .map(mapper::toReceiptResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptById(Long id) {
        Receipt receipt = receiptRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Phiếu nhập kho không tồn tại"));
        return mapper.toReceiptResponse(receipt);
    }

    @Transactional
    public void deleteReceipt(Long id) {
        Receipt receipt = receiptRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Phiếu nhập kho không tồn tại"));

        // Hoàn lại tồn kho trước khi xóa
        for (ReceiptItem item : receipt.getItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
            bookRepo.save(book);

            // Ghi inventory OUT
            invRepo.save(factory.createInventory(book, item.getQuantity(), InventoryType.OUT,
                    "Hủy phiếu nhập - Phiếu " + receipt.getReceiptCode()));
        }

        receiptRepo.delete(receipt);
        log.info("Deleted receipt with ID: {}", id);
    }
}