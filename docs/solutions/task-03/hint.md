# Task 3 Solution Hint: Update Cart Item

## Implementation Approach

### Service Layer
```java
@Autowired
private ProductRepository productRepository;

public CartItemResponse updateCartItem(Long id, CartItemRequest request) {
    // 1. Fetch existing cart item
    CartItem cartItem = cartItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", id));
    
    // 2. Validate new product exists
    Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));
    
    // 3. Update cart item
    cartItem.setProductId(product.getId());
    cartItem.setProductName(product.getName());
    cartItem.setPrice(product.getPrice());
    cartItem.setQuantity(request.getQuantity());
    if (request.getOrderId() != null) {
        cartItem.setOrderId(request.getOrderId());
    }
    
    // 4. Save and return
    CartItem updatedItem = cartItemRepository.save(cartItem);
    return mapToResponse(updatedItem);
}
```

### Controller Layer
```java
@PutMapping("/{id}")
public CartItemResponse updateCartItem(
        @PathVariable Long id,
        @Valid @RequestBody CartItemRequest request) {
    return cartItemService.updateCartItem(id, request);
}
```

## Key Points
- Fetch existing item first (404 if not found)
- Validate new product exists (404 if not found)
- Update all relevant fields
- Use PUT for full resource update
