package com.college.e_commarce.service.impl;

import com.college.e_commarce.dto.ProductCreateDto;
import com.college.e_commarce.dto.ProductResponseDto;
import com.college.e_commarce.entity.Product;
import com.college.e_commarce.entity.User;
import com.college.e_commarce.repository.ProductRepository;
import com.college.e_commarce.repository.UserRepository;
import com.college.e_commarce.service.ProductService;
import com.college.e_commarce.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product productExists = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductResponseDto.builder()
                .id(productExists.getId())
                .name(productExists.getName())
                .description(productExists.getDescription())
                .price(productExists.getPrice())
                .stock(productExists.getStock())
                .sold((productExists.getSold()))
                .image(productExists.getImage())
                .ratings(productExists.getRatings())
                .noOfReview(productExists.getNoOfReview())
                .totalRatings(productExists.getTotalRatings())
                .build();
    }

    @Override
    public ProductResponseDto getProductByName(String name) {
        Product productExists = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductResponseDto.builder()
                .id(productExists.getId())
                .name(productExists.getName())
                .description(productExists.getDescription())
                .price(productExists.getPrice())
                .stock(productExists.getStock())
                .sold(productExists.getSold())
                .image(productExists.getImage())
                .ratings(productExists.getRatings())
                .noOfReview(productExists.getNoOfReview())
                .totalRatings(productExists.getTotalRatings())
                .build();
    }

    @Override
    public List<ProductResponseDto> searchProduct(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }

        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(product -> ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .sold(product.getSold())
                        .image(product.getImage())
                        .ratings(product.getRatings())
                        .noOfReview(product.getNoOfReview())
                        .totalRatings(product.getTotalRatings())
                        .build()
                )
                .toList();
    }

    @Override
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable)
                .map(product -> ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .sold(product.getSold())
                        .image(product.getImage())
                        .ratings(product.getRatings())
                        .noOfReview(product.getNoOfReview())
                        .totalRatings(product.getTotalRatings())
                        .build()
                );
    }

}
