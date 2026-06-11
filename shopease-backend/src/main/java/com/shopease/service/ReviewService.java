package com.shopease.service;

import com.shopease.entity.Review;
import com.shopease.entity.Product;
import com.shopease.entity.User;
import com.shopease.repository.ReviewRepository;
import com.shopease.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    public Review addReview(User buyer, Long productId,
                            Integer rating, String comment) {
        if (!orderService.hasOrdered(buyer, productId)) {
            throw new RuntimeException(
                    "You can only review products you have ordered!");
        }
        if (reviewRepository.existsByBuyerIdAndProductId(
                buyer.getId(), productId)) {
            throw new RuntimeException(
                    "You have already reviewed this product!");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
        Review review = new Review();
        review.setBuyer(buyer);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Double getAverageRating(Long productId) {
        List<Review> reviews =
                reviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average().orElse(0.0);
    }
}