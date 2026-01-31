# Task 8 Solution Hint: Filter Orders by Status

## Implementation Approach

### 1. Add Query Method to OrderRepository
```java
public interface OrderRepository extends JpaRepository<Order, Long>, 
                                          JpaSpecificationExecutor<Order> {
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
```

### 2. Service Layer Implementation
```java
public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
    Page<Order> orderPage = orderRepository.findByStatus(status, pageable);
    return orderPage.map(this::mapToResponse);
}

// Alternative: Using MapStruct (Provided in Skeleton)
@Autowired
private OrderMapper orderMapper;

public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
    Page<Order> orderPage = orderRepository.findByStatus(status, pageable);
    return orderPage.map(orderMapper::toResponse);  // Automatic mapping
}
```

### 3. Controller Layer Implementation

**Option 1: Separate endpoint**
```java
@GetMapping
public Page<OrderResponse> getOrders(
        @RequestParam(required = false) OrderStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    
    if (status != null) {
        return orderService.getOrdersByStatus(status, pageable);
    } else {
        return orderService.getOrders(pageable);
    }
}
```

**Option 2: Using Spring's automatic Pageable**
```java
@GetMapping
public Page<OrderResponse> getOrders(
        @RequestParam(required = false) OrderStatus status,
        Pageable pageable) {
    
    if (status != null) {
        return orderService.getOrdersByStatus(status, pageable);
    }
    return orderService.getOrders(pageable);
}
```

## Testing
```bash
# All orders
curl "http://localhost:8080/api/orders?page=0&size=10"

# Filter by status
curl "http://localhost:8080/api/orders?status=CONFIRMED&page=0&size=10"

# With sorting
curl "http://localhost:8080/api/orders?status=SHIPPED&sort=orderDate,desc"

mvn test -Dtest=Task08FilterOrdersByStatusTest
```

## Key Points
- Spring Data JPA auto-generates query from method name
- Use `@RequestParam(required = false)` for optional filtering
- Combine with pagination seamlessly
- Enum parameters are automatically converted by Spring
