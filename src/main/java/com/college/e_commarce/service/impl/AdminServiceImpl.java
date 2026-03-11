package com.college.e_commarce.service.impl;

import com.college.e_commarce.dto.ProductResponseDto;
import com.college.e_commarce.dto.UsersResponseDto;
import com.college.e_commarce.entity.Product;
import com.college.e_commarce.entity.User;
import com.college.e_commarce.enums.Role;
import com.college.e_commarce.enums.UserStatus;
import com.college.e_commarce.repository.CartProductRepository;
import com.college.e_commarce.repository.OrderRepository;
import com.college.e_commarce.repository.ProductRepository;
import com.college.e_commarce.repository.UserRepository;
import com.college.e_commarce.service.AdminService;
import com.college.e_commarce.util.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;
    private final OrderRepository orderRepository;
    private final AuthUtil authUtil;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UsersResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    return UsersResponseDto.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .address(user.getAddress())
                            .role(user.getRole())
                            .active(user.getActive())
                            .build();
                })
                .toList();
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    return ProductResponseDto.builder()
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
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin not blockable");
        }

        if (user.getActive() == UserStatus.ACTIVE) {
            user.setActive(UserStatus.BLOCKED);
        } else {
            user.setActive(UserStatus.ACTIVE);
        }
    }

    @Transactional
    @Override
    public void deleteProductById(Long productId) {

        User currentUser = authUtil.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // remove references
        cartProductRepository.deleteByProductId(productId);
        orderRepository.deleteByProductId(productId);

        entityManager.flush();
        entityManager.clear();   // ⭐ important

        // delete product
        productRepository.delete(product);
    }
}
