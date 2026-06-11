package com.shopease.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    @JsonIgnoreProperties({"roles","authorities","password",
            "enabled","phone","accountNonExpired",
            "accountNonLocked","credentialsNonExpired","username"})
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"seller","category",
            "description","stock","createdAt"})
    private Product product;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}