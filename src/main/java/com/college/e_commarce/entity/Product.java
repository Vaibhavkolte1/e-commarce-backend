package com.college.e_commarce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    private int sold = 0;

    private String image;

    private float ratings = 0;

    private int noOfReview = 0;

    private int totalRatings = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @ToString.Exclude
    private User seller;

    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    private List<CartProduct> cartProducts = new ArrayList<>();
}


