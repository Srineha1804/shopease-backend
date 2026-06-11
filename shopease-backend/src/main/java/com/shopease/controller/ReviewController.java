package com.shopease.controller;

import com.shopease.entity.Review;
import com.shopease.entity.User;
import com.shopease.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{productId}")
    public ResponseEntity<Review> addReview(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User user) {
        Integer rating = Integer.valueOf(
                body.get("rating").toString());
        String comment = body.get("comment").toString();
        return ResponseEntity.ok(
                reviewService.addReview(user, productId,
                        rating, comment));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<Review>> getReviews(
            @PathVariable Long productId) {
        return ResponseEntity.ok(
                reviewService.getProductReviews(productId));
    }

    @GetMapping("/{productId}/average")
    public ResponseEntity<Map<String, Double>> getAverage(
            @PathVariable Long productId) {
        return ResponseEntity.ok(Map.of("average",
                reviewService.getAverageRating(productId)));
    }
}