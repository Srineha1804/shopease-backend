package com.shopease.controller;

import com.shopease.entity.Category;
import com.shopease.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(
                categoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Category> create(
            @RequestBody Category category) {
        return ResponseEntity.ok(
                categoryRepository.save(category));
    }
}