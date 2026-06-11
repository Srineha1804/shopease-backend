package com.shopease.controller;

import com.shopease.dto.ProductRequest;
import com.shopease.entity.Product;
import com.shopease.entity.User;
import com.shopease.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(
                productService.getAllProducts(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Product> create(
            @Valid @RequestBody ProductRequest req,
            @AuthenticationPrincipal User seller) {
        return ResponseEntity.ok(
                productService.createProduct(req, seller));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest req,
            @AuthenticationPrincipal User seller) {
        return ResponseEntity.ok(
                productService.updateProduct(id, req, seller));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User seller) {
        productService.deleteProduct(id, seller);
        return ResponseEntity.ok("Product deleted!");
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<Product>> myProducts(
            @AuthenticationPrincipal User seller) {
        return ResponseEntity.ok(
                productService.getProductsBySeller(seller.getId()));
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal User seller) {
        return ResponseEntity.ok(
                productService.updateStock(
                        id, body.get("stock"), seller));
    }
}