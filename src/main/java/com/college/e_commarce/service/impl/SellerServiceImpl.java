package com.college.e_commarce.service.impl;

import com.college.e_commarce.dto.ProductCreateDto;
import com.college.e_commarce.dto.ProductResponseDto;
import com.college.e_commarce.entity.Product;
import com.college.e_commarce.entity.User;
import com.college.e_commarce.repository.CartProductRepository;
import com.college.e_commarce.repository.OrderRepository;
import com.college.e_commarce.repository.ProductRepository;
import com.college.e_commarce.service.SellerService;
import com.college.e_commarce.util.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;
    private final OrderRepository orderRepository;
    private final AuthUtil authUtil;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProductResponseDto> getAllMyProducts() {
        User currentUser = authUtil.getCurrentUser();

        return productRepository.findBySeller(currentUser)
                        .stream()
                        .map(product -> {
                            return ProductResponseDto.builder()
                                    .id(product.getId())
                                    .name(product.getName())
                                    .description(product.getDescription())
                                    .price(product.getPrice())
                                    .stock(product.getStock())
                                    .sold((product.getSold()))
                                    .image(product.getImage())
                                    .ratings(product.getRatings())
                                    .noOfReview(product.getNoOfReview())
                                    .totalRatings(product.getTotalRatings())
                                    .build();
                        }).toList();
    }

    @Override
    public ProductResponseDto createProduct(ProductCreateDto dto) {
        if(productRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Product already exists.");
        }

        User seller = authUtil.getCurrentUser();

        Product newProduct = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .image(dto.getImage())
                .createdAt(LocalDateTime.now())
                .seller(seller)
                .build();

        Product saveProduct = productRepository.save(newProduct);

        return ProductResponseDto.builder()
                .id(saveProduct.getId())
                .name(saveProduct.getName())
                .description(saveProduct.getDescription())
                .price(saveProduct.getPrice())
                .stock(saveProduct.getStock())
                .sold(saveProduct.getSold())
                .image(saveProduct.getImage())
                .ratings(saveProduct.getRatings())
                .noOfReview(saveProduct.getNoOfReview())
                .totalRatings(saveProduct.getTotalRatings())
                .build();
    }

    @Transactional
    @Override
    public void deleteProductById(Long productId) {

        User currentUser = authUtil.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getEmail().equals(currentUser.getEmail())) {
            throw new RuntimeException("Unauthorized seller");
        }

        // remove references
        cartProductRepository.deleteByProductId(productId);
        orderRepository.deleteByProductId(productId);

        entityManager.flush();
        entityManager.clear();   // ⭐ important

        // delete product
        productRepository.delete(product);
    }

}
