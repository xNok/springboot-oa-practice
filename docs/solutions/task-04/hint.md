# Task 4 Solution Hint: Delete Cart Item

## Implementation Approach

### Service Layer
```java
public void deleteCartItem(Long id) {
    // Check if cart item exists
    if (!cartItemRepository.existsById(id)) {
        throw new ResourceNotFoundException("CartItem", id);
    }
    
    // Delete the cart item
    cartItemRepository.deleteById(id);
}
```

### Alternative: Using findById
```java
public void deleteCartItem(Long id) {
    CartItem cartItem = cartItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", id));
    
    cartItemRepository.delete(cartItem);
}
```

### Controller Layer
```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteCartItem(@PathVariable Long id) {
    cartItemService.deleteCartItem(id);
}
```

### Alternative: Using ResponseEntity
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
    cartItemService.deleteCartItem(id);
    return ResponseEntity.noContent().build();
}
```

## Key Points
- Return 204 No Content (no response body)
- Validate resource exists before deleting (404 if not found)
- `existsById()` is more efficient than `findById()` if you don't need the entity
