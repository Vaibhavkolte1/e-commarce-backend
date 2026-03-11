package com.college.e_commarce.repository;

import com.college.e_commarce.entity.Cart;
import com.college.e_commarce.entity.CartProduct;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    Optional<CartProduct> findByIdAndCart(Long id, Cart cart);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartProduct cp WHERE cp.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
