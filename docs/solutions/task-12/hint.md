# Task 12 Solution Hint: Cancel Order

## Implementation Approach

### Service Layer Implementation
```java
public OrderResponse cancelOrder(Long id) {
    // 1. Fetch order
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    
    // 2. Check current status
    OrderStatus currentStatus = order.getStatus();
    
    // 3. Validate cancellation rules
    if (currentStatus == OrderStatus.DELIVERED) {
        throw new BusinessRuleException("Cannot cancel a delivered order");
    }
    
    if (currentStatus == OrderStatus.CANCELLED) {
        // Option 1: Idempotent - return current state
        return mapToResponse(order);
        
        // Option 2: Throw exception
        // throw new BusinessRuleException("Order is already cancelled");
    }
    
    // 4. Update status to CANCELLED
    order.setStatus(OrderStatus.CANCELLED);
    
    // 5. Save and return
    Order updatedOrder = orderRepository.save(order);
    return mapToResponse(updatedOrder);
}
```

### Alternative: Reuse updateOrderStatus
```java
public OrderResponse cancelOrder(Long id) {
    UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
    request.setStatus(OrderStatus.CANCELLED);
    
    return updateOrderStatus(id, request);
}
```

### Controller Layer Implementation
```java
@PostMapping("/{id}/cancel")
public OrderResponse cancelOrder(@PathVariable Long id) {
    return orderService.cancelOrder(id);
}
```

## Business Rules

### Can Cancel:
- ✅ CREATED → CANCELLED
- ✅ CONFIRMED → CANCELLED  
- ✅ SHIPPED → CANCELLED

### Cannot Cancel:
- ❌ DELIVERED (order completed)
- ❌ Already CANCELLED (optional: make idempotent)

## Idempotency Consideration

**Option 1: Idempotent (Recommended)**
```java
if (currentStatus == OrderStatus.CANCELLED) {
    // Already cancelled - return current state
    return mapToResponse(order);
}
```

**Option 2: Strict**
```java
if (currentStatus == OrderStatus.CANCELLED) {
    throw new BusinessRuleException("Order is already cancelled");
}
```

Idempotent approach is generally better for REST APIs - calling cancel multiple times has the same effect.

## Testing

```bash
# Cancel a CREATED order (success)
curl -X POST http://localhost:8080/api/orders/1/cancel

# Cancel a CONFIRMED order (success)
curl -X POST http://localhost:8080/api/orders/2/cancel

# Cancel a DELIVERED order (error 422)
curl -X POST http://localhost:8080/api/orders/3/cancel

# Cancel already CANCELLED order (idempotent - success)
curl -X POST http://localhost:8080/api/orders/1/cancel

mvn test -Dtest=Task12CancelOrderTest
```

## Expected Responses

### Success (200 OK)
```json
{
  "id": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T10:30:00",
  "status": "CANCELLED",
  "totalAmount": 2029.97
}
```

### Cannot Cancel DELIVERED (422)
```json
{
  "message": "Cannot cancel a delivered order",
  "status": 422,
  "timestamp": "2026-01-31T11:00:00",
  "errors": []
}
```

## Key Points

1. **Dedicated endpoint**: `/orders/{id}/cancel` is more semantic than PATCH with status
2. **Simplified API**: Clients don't need to specify the status
3. **Business logic**: Encapsulates cancellation rules in one place
4. **Idempotency**: Consider making it safe to call multiple times
5. **Use POST**: Represents an action, not just a state change

## When to Use This vs updateOrderStatus

- **Use cancel endpoint**: For user-facing cancellation feature
- **Use updateOrderStatus**: For administrative status changes through workflow
- Both can coexist: cancel is a convenience wrapper
