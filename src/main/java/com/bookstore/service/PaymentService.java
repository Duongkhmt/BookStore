package com.bookstore.service;

import com.bookstore.dto.request.UpdatePaymentStatusRequest;
import com.bookstore.dto.response.PaymentResponse;
import com.bookstore.entity.*;
import com.bookstore.repository.*;
import com.bookstore.service.helper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository payRepo;
    private final OrderRepository orderRepo;
    private final EntityFinder finder;
    private final EntityMapper mapper;

    public PaymentService(PaymentRepository payRepo, OrderRepository orderRepo,
                          EntityFinder finder, EntityMapper mapper) {
        this.payRepo = payRepo;
        this.orderRepo = orderRepo;
        this.finder = finder;
        this.mapper = mapper;
    }

    @Transactional
    public PaymentResponse updatePaymentStatus(Long id, UpdatePaymentStatusRequest req) {
        // SỬA: Dùng EntityFinder
        Payment payment = finder.findPayment(id);

        PaymentStatus newStatus = PaymentStatus.valueOf(req.getNewStatus().toUpperCase());

        payment.setStatus(newStatus);
        if (req.getTransactionId() != null) {
            payment.setTransactionId(req.getTransactionId());
        }

        Order order = payment.getOrder();
        if (newStatus == PaymentStatus.COMPLETED) {
            order.setStatus(OrderStatus.PROCESSING);
        } else if (newStatus == PaymentStatus.FAILED || newStatus == PaymentStatus.REFUNDED) {
            order.setStatus(OrderStatus.CANCELLED);
        }

        orderRepo.save(order);
        return mapper.toPaymentResponse(payRepo.save(payment));
    }

    public PaymentResponse getPaymentDetails(Long id) {
        // SỬA: Dùng EntityFinder
        Payment payment = finder.findPayment(id);
        return mapper.toPaymentResponse(payment);
    }
}