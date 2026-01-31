# Task 6 Solution Hint: Get Order by ID

## Overview
Implement a GET endpoint to retrieve a single order by ID with proper error handling.

## Key Concepts
- findById with Optional handling
- ResourceNotFoundException for 404 responses
- Path variables

## Implementation Approach

### 1. Service Layer Implementation

```java
public OrderResponse getOrderById(Long id) {
    // Fetch order or throw exception
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    
    // Map to response
    return mapToResponse(order);
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
@GetMapping("/{id}")
public OrderResponse getOrderById(@PathVariable Long id) {
    return orderService.getOrderById(id);
}
```

## Common Pitfalls

1. **Not handling Optional properly**
   - ❌ `Order order = orderRepository.findById(id).get();` (throws NoSuchElementException)
   - ✅ Use `.orElseThrow(() -> new ResourceNotFoundException(...))`

2. **Wrong exception type**
   - Use ResourceNotFoundException, not custom exceptions

3. **Missing @PathVariable**
   - Required to bind URL parameter to method parameter

## Alternative Implementations

### Using Optional.map
```java
public OrderResponse getOrderById(Long id) {
    return orderRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
}
```

### Using MapStruct (Provided in Skeleton)
```java
@Autowired
private OrderMapper orderMapper;

public OrderResponse getOrderById(Long id) {
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    return orderMapper.toResponse(order);  // Automatic mapping
}

// Or with Optional.map:
public OrderResponse getOrderById(Long id) {
    return orderRepository.findById(id)
            .map(orderMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
}
```

### Custom Error Message
```java
.orElseThrow(() -> new ResourceNotFoundException(
    String.format("Order with ID %d not found", id)
))
```

## Testing

```bash
# Successful retrieval
curl http://localhost:8080/api/orders/1

# Not found error
curl http://localhost:8080/api/orders/9999

mvn test -Dtest=Task06GetOrderByIdTest
```

## Expected Responses

### Success (200 OK)
```json
{
  "id": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T10:30:00",
  "status": "CREATED",
  "totalAmount": 2029.97
}
```

### Not Found (404)
```json
{
  "message": "Order not found with id: 9999",
  "status": 404,
  "timestamp": "2026-01-31T11:00:00",
  "errors": []
}
```
