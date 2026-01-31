# Task 13 Solution Hint - Checkout (Create Order from Cart)

## Key Implementation Steps

### 1. Repository Query Method
Add a custom query method to `CartItemRepository`:
```java
List<CartItem> findByOrderIdIsNull();
```
This finds all cart items that haven't been assigned to an order yet.

### 2. Service Layer Implementation
```java
@Transactional
public OrderResponse checkout(CheckoutRequest request) {
    // 1. Fetch all cart items (orderId = null)
    List<CartItem> cartItems = cartItemRepository.findByOrderIdIsNull();
    
    // 2. Validate cart is not empty
    if (cartItems.isEmpty()) {
        throw new BusinessRuleException("Cannot checkout with an empty cart");
    }
    
    // 3. Calculate total from cart items
    BigDecimal totalAmount = cartItems.stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // 4. Create the order
    Order order = new Order();
    order.setCustomerId(request.getCustomerId());
    order.setCustomerName(request.getCustomerName());
    order.setOrderDate(LocalDateTime.now());
    order.setStatus(OrderStatus.CREATED);
    order.setTotalAmount(totalAmount);
    
    Order savedOrder = orderRepository.save(order);
    
    // 5. Link cart items to the order
    cartItems.forEach(item -> item.setOrderId(savedOrder.getId()));
    cartItemRepository.saveAll(cartItems);
    
    // 6. Convert to response DTO
    return convertToOrderResponse(savedOrder);
}

// Alternative: Using MapStruct (Provided in Skeleton)
@Autowired
private OrderMapper orderMapper;
@Autowired
private CartItemMapper cartItemMapper;

@Transactional
public OrderResponse checkout(CheckoutRequest request) {
    List<CartItem> cartItems = cartItemRepository.findByOrderIdIsNull();
    
    if (cartItems.isEmpty()) {
        throw new BusinessRuleException("Cannot checkout with an empty cart");
    }
    
    BigDecimal totalAmount = cartItems.stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    Order order = new Order();
    order.setCustomerId(request.getCustomerId());
    order.setCustomerName(request.getCustomerName());
    order.setOrderDate(LocalDateTime.now());
    order.setStatus(OrderStatus.CREATED);
    order.setTotalAmount(totalAmount);
    
    Order savedOrder = orderRepository.save(order);
    
    cartItems.forEach(item -> item.setOrderId(savedOrder.getId()));
    cartItemRepository.saveAll(cartItems);
    
    return orderMapper.toResponse(savedOrder);  // Automatic mapping
}
```

### 3. Controller Endpoint
```java
@PostMapping("/checkout")
public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
    OrderResponse order = orderService.checkout(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
}
```

## Common Pitfalls

1. **Forgetting @Transactional**
   - Both creating the order AND updating cart items must be atomic
   - If one fails, both should rollback

2. **Not Validating Empty Cart**
   - Should throw `BusinessRuleException` with HTTP 422 if cart is empty
   - Better user experience than creating an order with 0 items

3. **Incorrect Total Calculation**
   - Must multiply price × quantity for each item
   - Use `BigDecimal` for monetary calculations to avoid rounding errors
   - Use `reduce` to sum all subtotals

4. **Not Clearing Cart Properly**
   - Setting `orderId` on cart items effectively "moves" them to the order
   - Future cart queries (orderId IS NULL) won't return these items

5. **Missing Validation on CheckoutRequest**
   - `customerId` is required (`@NotNull`)
   - `customerName` can be optional (might fetch from customer service)

## Testing Strategy

```java
@Test
@Sql("/test-data/base-data.sql")
@Sql("/test-data/cart-items.sql")
void testCheckoutSuccess() throws Exception {
    CheckoutRequest request = new CheckoutRequest();
    request.setCustomerId(1L);
    request.setCustomerName("John Doe");
    
    mockMvc.perform(post("/api/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.customerId").value(1))
        .andExpect(jsonPath("$.status").value("CREATED"))
        .andExpect(jsonPath("$.totalAmount").value(2109.96));
    
    // Verify cart is now empty
    mockMvc.perform(get("/api/cart/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
}

@Test
void testCheckoutEmptyCart() throws Exception {
    CheckoutRequest request = new CheckoutRequest();
    request.setCustomerId(1L);
    request.setCustomerName("John Doe");
    
    mockMvc.perform(post("/api/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("Cannot checkout with an empty cart"));
}

@Test
void testCheckoutValidation() throws Exception {
    CheckoutRequest request = new CheckoutRequest();
    // Missing required customerId
    
    mockMvc.perform(post("/api/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
}
```

## Integration with Other Tasks

- **Relates to Tasks 1-4**: Checkout consumes cart items created in those tasks
- **Relates to Task 5**: Alternative way to create an order (from cart vs. direct creation)
- **Relates to Task 10-12**: Order created via checkout follows same state transitions
- **Real-world flow**: Add to cart (T2) → Update quantities (T3) → Remove unwanted items (T4) → Checkout (T13)

## API Design Rationale

Why `/api/cart/checkout` instead of `/api/orders`?
- Checkout is a **cart-level operation**, not just order creation
- It modifies cart state (clears items by linking them)
- Separates business logic: "create empty order" vs "convert cart to order"
- Allows for future cart operations like `/api/cart/clear`

This design is more RESTful and extensible for real e-commerce applications.
