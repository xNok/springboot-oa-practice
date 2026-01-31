# Task 1 Solution Hint: Retrieve All Cart Items

## Overview
This task requires implementing a simple GET endpoint that retrieves all cart items from the database and maps them to DTOs.

## Key Concepts
- Repository pattern with Spring Data JPA
- Entity to DTO mapping
- Calculated fields (subtotal)

## Implementation Approach

### 1. Service Layer Implementation

```java
public List<CartItemResponse> getAllCartItems() {
    List<CartItem> cartItems = cartItemRepository.findAll();
    
    return cartItems.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}

private CartItemResponse mapToResponse(CartItem cartItem) {
    CartItemResponse response = new CartItemResponse();
    response.setId(cartItem.getId());
    response.setOrderId(cartItem.getOrderId());
    response.setProductId(cartItem.getProductId());
    response.setProductName(cartItem.getProductName());
    response.setQuantity(cartItem.getQuantity());
    response.setPrice(cartItem.getPrice());
    response.setSubtotal(cartItem.getQuantity() * cartItem.getPrice());
    
    return response;
}
```

### 2. Controller Layer Implementation

```java
@GetMapping
public List<CartItemResponse> getAllCartItems() {
    return cartItemService.getAllCartItems();
}
```

## Common Pitfalls

1. **Returning null instead of empty list**
   - ❌ `if (cartItems.isEmpty()) return null;`
   - ✅ Return empty list directly

2. **Forgetting to calculate subtotal**
   - Remember: `subtotal = quantity * price`

3. **Not mapping entity to DTO**
   - ❌ Returning CartItem entities directly
   - ✅ Map to CartItemResponse DTOs

## Alternative Implementations

### Using MapStruct (Provided in Skeleton)
```java
// The skeleton includes CartItemMapper with automatic null-safe mapping
@Autowired
private CartItemMapper cartItemMapper;

public List<CartItemResponse> getAllCartItems() {
    return cartItemRepository.findAll().stream()
            .map(cartItemMapper::toResponse)  // Auto-calculates subtotal
            .collect(Collectors.toList());
}

// For extra safety with potentially null entities:
// .map(cartItemMapper::toResponseSafe)
```

### Using ModelMapper
```java
// If you prefer using a different mapping library (not required for OA)
@Autowired
private ModelMapper modelMapper;

private CartItemResponse mapToResponse(CartItem cartItem) {
    CartItemResponse response = modelMapper.map(cartItem, CartItemResponse.class);
    response.setSubtotal(cartItem.getQuantity() * cartItem.getPrice());
    return response;
}
```

### Inline Mapping
```java
public List<CartItemResponse> getAllCartItems() {
    return cartItemRepository.findAll().stream()
            .map(item -> new CartItemResponse(
                item.getId(),
                item.getOrderId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice(),
                item.getQuantity() * item.getPrice()
            ))
            .collect(Collectors.toList());
}
```

## Testing the Implementation

```bash
# Start the application
mvn spring-boot:run

# Test with curl
curl http://localhost:8080/api/cart-items

# Run the test
mvn test -Dtest=Task01GetAllCartItemsTest
```

## Expected Complexity
- Time Complexity: O(n) where n is the number of cart items
- Space Complexity: O(n) for the result list
