package com.example.oa.repository;

import com.example.oa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Product repository interface (STUB for OA practice).
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
