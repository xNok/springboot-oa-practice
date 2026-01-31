package com.example.oa.repository;

import com.example.oa.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CartItem repository interface.
 * 
 * This interface is provided as part of the skeleton.
 * Candidates may need to add custom query methods.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // TODO: Candidates may add custom query methods if needed
    // Example: List<CartItem> findByOrderId(Long orderId);
    
    // Hint for Task 13: You'll need to find cart items that are not yet in an order
    // List<CartItem> findByOrderIdIsNull();
}

