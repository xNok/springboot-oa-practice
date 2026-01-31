package com.example.oa.service;

import com.example.oa.dto.CartItemRequest;
import com.example.oa.dto.CartItemResponse;
import com.example.oa.entity.CartItem;
import com.example.oa.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CartItem service layer.
 * 
 * Candidates should implement the business logic in this service.
 * 
 * TODO: Implement all CRUD operations (Tasks 1-4)
 */
@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    // Task 1 - Retrieve all cart items
    public List<CartItemResponse> getAllCartItems() {
        return cartItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    private CartItemResponse mapToResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getOrderId(),
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                cartItem.getQuantity() * cartItem.getPrice()
        );
    }

    // TODO: Task 2 - Implement method to add a new cart item
    public CartItemResponse addCartItem(CartItemRequest request) {
        throw new UnsupportedOperationException("Task 2: Implement addCartItem");
    }

    // TODO: Task 3 - Implement method to update an existing cart item
    public CartItemResponse updateCartItem(Long id, CartItemRequest request) {
        throw new UnsupportedOperationException("Task 3: Implement updateCartItem");
    }

    // TODO: Task 4 - Implement method to delete a cart item
    public void deleteCartItem(Long id) {
        throw new UnsupportedOperationException("Task 4: Implement deleteCartItem");
    }
}
