package com.college.e_commarce.repository;

import com.college.e_commarce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBuyer_Id(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Order o WHERE o.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
