# Task 2 Solution Hint: Add Cart Item

## Overview
This task requires implementing a POST endpoint that validates input, checks product existence, and creates a new cart item.

## Key Concepts
- Request validation with @Valid
- Resource existence validation
- Exception handling for not found resources
- HTTP 201 Created status

## Implementation Approach

### 1. Service Layer Implementation

```java
@Autowired
private ProductRepository productRepository;

public CartItemResponse addCartItem(CartItemRequest request) {
    // 1. Validate that product exists
    Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));
    
    // 2. Create new cart item entity
    CartItem cartItem = new CartItem();
    cartItem.setProductId(product.getId());
    cartItem.setProductName(product.getName());
    cartItem.setPrice(product.getPrice());
    cartItem.setQuantity(request.getQuantity());
    cartItem.setOrderId(request.getOrderId()); // May be null initially
    
    // 3. Save to database
    CartItem savedItem = cartItemRepository.save(cartItem);
    
    // 4. Map to response with calculated subtotal
    return mapToResponse(savedItem);
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
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public CartItemResponse addCartItem(@Valid @RequestBody CartItemRequest request) {
    return cartItemService.addCartItem(request);
}
```

## Common Pitfalls

1. **Not injecting ProductRepository**
   - Remember to add `@Autowired private ProductRepository productRepository;` in the service

2. **Wrong HTTP status code**
   - ❌ Returning 200 OK
   - ✅ Use `@ResponseStatus(HttpStatus.CREATED)` or return `ResponseEntity` with 201

3. **Not validating product existence**
   - Always check if product exists before creating cart item

4. **Forgetting @Valid annotation**
   - ❌ `@RequestBody CartItemRequest request`
   - ✅ `@Valid @RequestBody CartItemRequest request`

5. **Not fetching product details**
   - You need to get product name and price from the Product entity

## Error Handling Examples

The GlobalExceptionHandler automatically handles:
- **400 Bad Request**: Invalid or missing fields (validation)
- **404 Not Found**: Product not found

```java
// This will be caught by GlobalExceptionHandler
throw new ResourceNotFoundException("Product", request.getProductId());
// Results in:
// {
//   "message": "Product not found with id: 999",
//   "status": 404,
//   "timestamp": "2026-01-31T10:00:00"
// }
```

## Alternative Implementation: Using ResponseEntity

```java
@PostMapping
public ResponseEntity<CartItemResponse> addCartItem(@Valid @RequestBody CartItemRequest request) {
    CartItemResponse response = cartItemService.addCartItem(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

## Testing the Implementation

```bash
# Test with curl
curl -X POST http://localhost:8080/api/cart-items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'

# Test validation error (missing fields)
curl -X POST http://localhost:8080/api/cart-items \
  -H "Content-Type: application/json" \
  -d '{"quantity": 2}'

# Test product not found
curl -X POST http://localhost:8080/api/cart-items \
  -H "Content-Type: application/json" \
  -d '{"productId": 9999, "quantity": 2}'

# Run the test
mvn test -Dtest=Task02AddCartItemTest
```

## Validation Annotations in CartItemRequest

The DTO already has validation:
```java
@NotNull(message = "Product ID is required")
private Long productId;

@NotNull(message = "Quantity is required")
@Positive(message = "Quantity must be positive")
private Integer quantity;
```

These are automatically enforced by @Valid in the controller.
