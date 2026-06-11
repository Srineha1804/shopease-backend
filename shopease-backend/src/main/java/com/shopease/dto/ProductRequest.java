package com.shopease.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String name;
    private String description;
    @Positive
    private Double price;
    private Integer stock;
    private Long categoryId;
    private String imageUrl;
}