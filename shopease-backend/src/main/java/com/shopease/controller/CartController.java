package com.shopease.controller;

import com.shopease.entity.CartItem;
import com.shopease.entity.User;
import com.shopease.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        return ResponseEntity.ok(
                cartService.addToCart(user, productId, quantity));
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartItem> updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(
                cartService.updateQuantity(user, itemId, body.get("quantity")));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId) {
        cartService.removeFromCart(user, itemId);
        return ResponseEntity.ok("Item removed!");
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Double>> getTotal(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                Map.of("total", cartService.getCartTotal(user)));
    }
}