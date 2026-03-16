package com.college.e_commarce.service;

import com.college.e_commarce.dto.ProductCreateDto;
import com.college.e_commarce.dto.ProductResponseDto;
import com.college.e_commarce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponseDto getProductById(Long id);

    ProductResponseDto getProductByName(String name);

    List<ProductResponseDto> searchProduct(String name);

    Page<ProductResponseDto> getAllProducts(Pageable pageable);

}
