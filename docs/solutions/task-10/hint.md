# Task 10 Solution Hint: Update Order Status

## Overview
Implement state transition validation and status update endpoint.

## Key Concepts
- State machine/transition validation
- BusinessRuleException for invalid transitions
- PATCH endpoint for partial updates

## Implementation Approach

### 1. Service Layer Implementation

```java
public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
    // 1. Fetch order
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    
    // 2. Validate transition
    OrderStatus currentStatus = order.getStatus();
    OrderStatus newStatus = request.getStatus();
    
    if (!isValidTransition(currentStatus, newStatus)) {
        throw new BusinessRuleException(
            String.format("Invalid state transition from %s to %s", 
                currentStatus, newStatus)
        );
    }
    
    // 3. Update status
    order.setStatus(newStatus);
    
    // 4. Save and return
    Order updatedOrder = orderRepository.save(order);
    return mapToResponse(updatedOrder);
}

// Alternative: Using MapStruct (Provided in Skeleton)
@Autowired
private OrderMapper orderMapper;

public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    
    OrderStatus currentStatus = order.getStatus();
    OrderStatus newStatus = request.getStatus();
    
    if (!isValidTransition(currentStatus, newStatus)) {
        throw new BusinessRuleException(
            String.format("Invalid state transition from %s to %s", 
                currentStatus, newStatus)
        );
    }
    
    order.setStatus(newStatus);
    Order updatedOrder = orderRepository.save(order);
    return orderMapper.toResponse(updatedOrder);  // Automatic mapping
}

private boolean isValidTransition(OrderStatus from, OrderStatus to) {
    // DELIVERED is final
    if (from == OrderStatus.DELIVERED) {
        return false;
    }
    
    // Cannot transition to CREATED
    if (to == OrderStatus.CREATED) {
        return false;
    }
    
    // CANCELLED is final
    if (from == OrderStatus.CANCELLED) {
        return false;
    }
    
    // Define valid transitions
    switch (from) {
        case CREATED:
            return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
        case CONFIRMED:
            return to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
        case SHIPPED:
            return to == OrderStatus.DELIVERED || to == OrderStatus.CANCELLED;
        default:
            return false;
    }
}
```

### 2. Alternative: Using Map for Transitions

```java
private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
    OrderStatus.CREATED, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
    OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
    OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
    OrderStatus.DELIVERED, Set.of(),
    OrderStatus.CANCELLED, Set.of()
);

private boolean isValidTransition(OrderStatus from, OrderStatus to) {
    return VALID_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
}
```

### 3. Controller Layer Implementation

```java
@PatchMapping("/{id}/status")
public OrderResponse updateOrderStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateOrderStatusRequest request) {
    return orderService.updateOrderStatus(id, request);
}
```

## State Transition Matrix

| From → To | CREATED | CONFIRMED | SHIPPED | DELIVERED | CANCELLED |
|-----------|---------|-----------|---------|-----------|-----------|
| **CREATED** | - | ✅ | ❌ | ❌ | ✅ |
| **CONFIRMED** | ❌ | - | ✅ | ❌ | ✅ |
| **SHIPPED** | ❌ | ❌ | - | ✅ | ✅ |
| **DELIVERED** | ❌ | ❌ | ❌ | - | ❌ |
| **CANCELLED** | ❌ | ❌ | ❌ | ❌ | - |

## Common Pitfalls

1. **Not validating transitions**
   - Always check if transition is valid before updating

2. **Wrong exception type**
   - Use BusinessRuleException for business logic violations
   - Use ResourceNotFoundException only for missing resources

3. **Forgetting final states**
   - DELIVERED and CANCELLED are final states (cannot transition out)

4. **Using POST instead of PATCH**
   - PATCH is semantically correct for partial updates

## Testing

```bash
# Valid transition: CREATED → CONFIRMED
curl -X PATCH http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'

# Invalid transition: DELIVERED → CANCELLED
curl -X PATCH http://localhost:8080/api/orders/2/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CANCELLED"}'

mvn test -Dtest=Task10UpdateOrderStatusTest
```

## Expected Responses

### Success (200 OK)
```json
{
  "id": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T10:30:00",
  "status": "CONFIRMED",
  "totalAmount": 2029.97
}
```

### Invalid Transition (422 Unprocessable Entity)
```json
{
  "message": "Invalid state transition from DELIVERED to CANCELLED",
  "status": 422,
  "timestamp": "2026-01-31T11:00:00",
  "errors": []
}
```

## Advanced: Enum-Based State Machine

For production code, consider a more robust approach:

```java
public enum OrderStatus {
    CREATED(Set.of(CONFIRMED, CANCELLED)),
    CONFIRMED(Set.of(SHIPPED, CANCELLED)),
    SHIPPED(Set.of(DELIVERED, CANCELLED)),
    DELIVERED(Set.of()),
    CANCELLED(Set.of());
    
    private final Set<OrderStatus> allowedTransitions;
    
    OrderStatus(Set<OrderStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }
    
    public boolean canTransitionTo(OrderStatus target) {
        return allowedTransitions.contains(target);
    }
}

// Usage in service:
if (!currentStatus.canTransitionTo(newStatus)) {
    throw new BusinessRuleException(...);
}
```
