package com.shopease.controller;

import com.shopease.entity.Order;
import com.shopease.entity.User;
import com.shopease.repository.OrderRepository;
import com.shopease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PutMapping("/users/{id}/toggle-ban")
    public ResponseEntity<String> toggleBan(
            @PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return ResponseEntity.ok(user.isEnabled()
                ? "User unbanned!" : "User banned!");
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        double totalRevenue = orderRepository.findAll()
                .stream()
                .filter(o -> o.getStatus() !=
                        Order.OrderStatus.CANCELLED)
                .mapToDouble(o -> o.getTotalAmount() != null
                        ? o.getTotalAmount() : 0)
                .sum();
        return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "totalOrders", totalOrders,
                "totalRevenue", totalRevenue
        ));
    }
}