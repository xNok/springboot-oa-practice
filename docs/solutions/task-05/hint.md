# Task 5 Solution Hint: Create Order

## Overview
Implement a POST endpoint to create a new order with initial status CREATED and current timestamp.

## Key Concepts
- Setting default values (status, timestamp)
- Entity creation and persistence
- HTTP 201 Created response

## Implementation Approach

### 1. Service Layer Implementation

```java
public OrderResponse createOrder(OrderRequest request) {
    // 1. Create new order entity
    Order order = new Order();
    order.setCustomerId(request.getCustomerId());
    order.setCustomerName(request.getCustomerName());
    order.setTotalAmount(request.getTotalAmount());
    
    // 2. Set defaults
    order.setOrderDate(LocalDateTime.now());
    order.setStatus(OrderStatus.CREATED);
    
    // 3. Save to database
    Order savedOrder = orderRepository.save(order);
    
    // 4. Map to response
    return mapToResponse(savedOrder);
}

private OrderResponse mapToResponse(Order order) {
    OrderResponse response = new OrderResponse();
    response.setId(order.getId());
    response.setCustomerId(order.getCustomerId());
    response.setCustomerName(order.getCustomerName());
    response.setOrderDate(order.getOrderDate());
    response.setStatus(order.getStatus());
    response.setTotalAmount(order.getTotalAmount());
    return response;
}
```

### 2. Controller Layer Implementation

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
    return orderService.createOrder(request);
}
```

## Common Pitfalls

1. **Not setting orderDate**
   - Always set: `order.setOrderDate(LocalDateTime.now())`

2. **Not setting initial status**
   - Always set: `order.setStatus(OrderStatus.CREATED)`

3. **Using wrong HTTP status**
   - ❌ 200 OK
   - ✅ 201 Created

4. **Forgetting @Valid annotation**
   - Required for validation

## Alternative: Using Builder Pattern

If you add `@Builder` to Order entity:
```java
Order order = Order.builder()
        .customerId(request.getCustomerId())
        .customerName(request.getCustomerName())
        .totalAmount(request.getTotalAmount())
        .orderDate(LocalDateTime.now())
        .status(OrderStatus.CREATED)
        .build();
```

## Alternative: Using MapStruct (Provided in Skeleton)

```java
@Autowired
private OrderMapper orderMapper;

public OrderResponse createOrder(OrderRequest request) {
    Order order = new Order();
    order.setCustomerId(request.getCustomerId());
    order.setCustomerName(request.getCustomerName());
    order.setTotalAmount(request.getTotalAmount());
    order.setOrderDate(LocalDateTime.now());
    order.setStatus(OrderStatus.CREATED);
    
    Order savedOrder = orderRepository.save(order);
    return orderMapper.toResponse(savedOrder);  // Automatic mapping
}
```

## Testing

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "customerName": "John Doe",
    "totalAmount": 2029.97
  }'

mvn test -Dtest=Task05CreateOrderTest
```
