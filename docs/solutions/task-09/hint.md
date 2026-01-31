# Task 9 Solution Hint: Filter Orders by Date Range

## Implementation Approach

### 1. Add Query Methods to OrderRepository
```java
public interface OrderRepository extends JpaRepository<Order, Long>, 
                                          JpaSpecificationExecutor<Order> {
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    Page<Order> findByOrderDateBetween(
        LocalDateTime start, 
        LocalDateTime end, 
        Pageable pageable
    );
    
    // Optional: for single-bound queries
    Page<Order> findByOrderDateAfter(LocalDateTime start, Pageable pageable);
    Page<Order> findByOrderDateBefore(LocalDateTime end, Pageable pageable);
}
```

### 2. Service Layer Implementation
```java
public Page<OrderResponse> getOrdersByDateRange(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable) {
    
    Page<Order> orderPage;
    
    if (startDate != null && endDate != null) {
        // Both dates provided
        orderPage = orderRepository.findByOrderDateBetween(startDate, endDate, pageable);
    } else if (startDate != null) {
        // Only start date
        orderPage = orderRepository.findByOrderDateAfter(startDate, pageable);
    } else if (endDate != null) {
        // Only end date
        orderPage = orderRepository.findByOrderDateBefore(endDate, pageable);
    } else {
        // No dates - return all
        orderPage = orderRepository.findAll(pageable);
    }
    
    return orderPage.map(this::mapToResponse);
}
```

### 3. Controller Layer Implementation
```java
@GetMapping
public Page<OrderResponse> getOrders(
        @RequestParam(required = false) OrderStatus status,
        @RequestParam(required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        LocalDateTime startDate,
        @RequestParam(required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        LocalDateTime endDate,
        Pageable pageable) {
    
    // If status filter is provided
    if (status != null) {
        return orderService.getOrdersByStatus(status, pageable);
    }
    
    // If date range is provided
    if (startDate != null || endDate != null) {
        return orderService.getOrdersByDateRange(startDate, endDate, pageable);
    }
    
    // Default: return all orders
    return orderService.getOrders(pageable);
}
```

## Alternative: Using Specifications (Advanced)

For more complex filtering combinations:

```java
public class OrderSpecifications {
    
    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);
    }
    
    public static Specification<Order> orderDateBetween(
            LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("orderDate"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("orderDate"), start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(root.get("orderDate"), end);
            }
            return null;
        };
    }
}

// Service method using Specifications
public Page<OrderResponse> getOrdersWithFilters(
        OrderStatus status, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable) {
    
    Specification<Order> spec = Specification
        .where(OrderSpecifications.hasStatus(status))
        .and(OrderSpecifications.orderDateBetween(startDate, endDate));
    
    Page<Order> orderPage = orderRepository.findAll(spec, pageable);
    return orderPage.map(this::mapToResponse);
}
```

## Testing
```bash
# Date range filter
curl "http://localhost:8080/api/orders?startDate=2026-01-01T00:00:00&endDate=2026-01-31T23:59:59"

# Only start date
curl "http://localhost:8080/api/orders?startDate=2026-01-15T00:00:00"

# Only end date
curl "http://localhost:8080/api/orders?endDate=2026-01-31T23:59:59"

# With pagination
curl "http://localhost:8080/api/orders?startDate=2026-01-01T00:00:00&page=0&size=10&sort=orderDate,desc"

mvn test -Dtest=Task09FilterOrdersByDateRangeTest
```

## Key Points
- Use `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)` for automatic parsing
- Date format: `yyyy-MM-ddTHH:mm:ss` (ISO 8601)
- Handle all combinations: both dates, only start, only end, neither
- `Between` is inclusive on both ends
- Consider timezone handling (UTC recommended)

## Common Pitfalls
1. **Not URL encoding dates in curl**: Use quotes around URLs with special characters
2. **Timezone issues**: Ensure consistent timezone usage (UTC in application.properties)
3. **Not handling null dates**: Always check for null before querying
4. **Wrong date format**: Must use ISO format with `@DateTimeFormat` annotation
