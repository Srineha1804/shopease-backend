package com.shopease.controller;

import com.shopease.entity.Order;
import com.shopease.entity.User;
import com.shopease.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                orderService.placeOrder(user,
                        body.get("shippingAddress")));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> myOrders(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                orderService.getMyOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                orderService.getOrderById(id, user));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Order.OrderStatus status =
                Order.OrderStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(
                orderService.updateStatus(id, status));
    }

    @GetMapping("/seller-orders")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<Order>> sellerOrders(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                orderService.getSellerOrders(user.getId()));
    }
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        orderService.cancelOrder(id, user);
        return ResponseEntity.ok("Order cancelled!");
    }
}