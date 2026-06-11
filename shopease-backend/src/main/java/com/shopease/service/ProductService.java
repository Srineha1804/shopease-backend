package com.shopease.service;

import com.shopease.dto.ProductRequest;
import com.shopease.entity.Product;
import com.shopease.entity.User;
import com.shopease.repository.CategoryRepository;
import com.shopease.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product createProduct(ProductRequest req, User seller) {
        Product p = new Product();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setImageUrl(req.getImageUrl());
        p.setSeller(seller);
        if (req.getCategoryId() != null) {
            categoryRepository.findById(req.getCategoryId())
                    .ifPresent(p::setCategory);
        }
        return productRepository.save(p);
    }

    public List<Product> getAllProducts(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return productRepository
                    .findByNameContainingIgnoreCase(keyword);
        }
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public Product updateProduct(Long id,
                                 ProductRequest req, User seller) {
        Product p = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
        if (!p.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("Not authorized!");
        }
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setImageUrl(req.getImageUrl());
        return productRepository.save(p);
    }

    public void deleteProduct(Long id, User seller) {
        Product p = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
        if (!p.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("Not authorized!");
        }
        productRepository.delete(p);
    }
    public Product updateStock(Long productId,
                               Integer newStock, User seller) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
        if (!p.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("Not authorized!");
        }
        p.setStock(newStock);
        return productRepository.save(p);
    }
}