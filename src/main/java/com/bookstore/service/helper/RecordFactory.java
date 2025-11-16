package com.bookstore.service.helper;

import com.bookstore.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RecordFactory {

    public Inventory createInventory(Book book, int quantity, InventoryType type, String reason) {
        Inventory inv = new Inventory();
        inv.setBook(book);
        inv.setQuantityChange(quantity);
        inv.setType(type);
        inv.setReason(reason);
        inv.setTimestamp(LocalDateTime.now());
        return inv;
    }

    public OrderTracking createTracking(Order order, OrderStatus status, String note) {
        OrderTracking track = new OrderTracking();
        track.setOrder(order);
        track.setStatus(status);
        track.setNote(note);
        track.setTimestamp(LocalDateTime.now());
        return track;
    }
}
