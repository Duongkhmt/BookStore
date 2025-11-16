package com.bookstore.security;

import com.bookstore.entity.Order;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@Slf4j
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepo;

    public boolean isOrderOwner(Long orderId, Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("🔐 Checking order ownership - User: {}, Order: {}", username, orderId);

            // Kiểm tra ADMIN role
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority ->
                            grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                log.info("✅ Admin {} granted access to order {}", username, orderId);
                return true;
            }

            // Kiểm tra quyền sở hữu
            Order order = orderRepo.findWithUserById(orderId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND,
                            "Không tìm thấy đơn hàng với ID: " + orderId));

            boolean isOwner = order.getUser().getUsername().equals(username);
            log.info("👤 User {} {} order {}", username, isOwner ? "owns" : "does not own", orderId);

            return isOwner;

        } catch (Exception e) {
            log.error("💥 Error checking order ownership for order {}: {}", orderId, e.getMessage());
            return false;
        }
    }
}