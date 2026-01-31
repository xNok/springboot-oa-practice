package com.example.oa.service;

import com.example.oa.dto.CartItemRequest;
import com.example.oa.dto.CartItemResponse;
import com.example.oa.entity.CartItem;
import com.example.oa.entity.Product;
import com.example.oa.exception.ResourceNotFoundException;
import com.example.oa.repository.CartItemRepository;
import com.example.oa.repository.ProductRepository;
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
    
    @Autowired
    private ProductRepository productRepository;

    // Task 1 - Retrieve all cart items
    public List<CartItemResponse> getAllCartItems() {
        return cartItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    private CartItemResponse mapToResponse(CartItem cartItem) {
        double subtotal = Math.round(cartItem.getQuantity() * cartItem.getPrice() * 100.0) / 100.0;
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getOrderId(),
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                subtotal
        );
    }

    // Task 2 - Add a new cart item
    public CartItemResponse addCartItem(CartItemRequest request) {
        // Validate that product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));
        
        // Create new cart item
        CartItem cartItem = new CartItem();
        cartItem.setProductId(product.getId());
        cartItem.setProductName(product.getName());
        cartItem.setPrice(product.getPrice());
        cartItem.setQuantity(request.getQuantity());
        cartItem.setOrderId(null);  // Cart items don't have an orderId until checkout
        
        // Save and return
        CartItem saved = cartItemRepository.save(cartItem);
        return mapToResponse(saved);
    }

    // Task 3 - Update an existing cart item
    public CartItemResponse updateCartItem(Long id, CartItemRequest request) {
        // Find existing cart item
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", id));
        
        // Validate that product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));
        
        // Update cart item fields
        cartItem.setProductId(product.getId());
        cartItem.setProductName(product.getName());
        cartItem.setPrice(product.getPrice());
        cartItem.setQuantity(request.getQuantity());
        
        // Save and return
        CartItem updated = cartItemRepository.save(cartItem);
        return mapToResponse(updated);
    }

    // Task 4 - Delete a cart item
    public void deleteCartItem(Long id) {
        // Check if cart item exists
        if (!cartItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cart item", id);
        }
        
        // Delete the cart item
        cartItemRepository.deleteById(id);
    }
}
