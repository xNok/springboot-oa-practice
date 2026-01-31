# Task 7 Solution Hint: Paginate Orders

## Overview
Implement pagination and sorting for the orders endpoint using Spring Data's Pageable.

## Key Concepts
- Pageable interface
- PageRequest creation
- Mapping Page<Entity> to Page<DTO>
- Query parameters

## Implementation Approach

### 1. Service Layer Implementation

```java
public Page<OrderResponse> getOrders(Pageable pageable) {
    // Fetch paginated orders
    Page<Order> orderPage = orderRepository.findAll(pageable);
    
    // Map entities to DTOs
    return orderPage.map(this::mapToResponse);
}

// Alternative: Using MapStruct (Provided in Skeleton)
@Autowired
private OrderMapper orderMapper;

public Page<OrderResponse> getOrders(Pageable pageable) {
    Page<Order> orderPage = orderRepository.findAll(pageable);
    return orderPage.map(orderMapper::toResponse);  // Automatic mapping
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
@GetMapping
public Page<OrderResponse> getOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {
    
    // Create Sort object
    Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
    
    // Create Pageable
    Pageable pageable = PageRequest.of(page, size, sort);
    
    return orderService.getOrders(pageable);
}
```

## Alternative: Simplified Controller

Spring can automatically create Pageable from query parameters:

```java
@GetMapping
public Page<OrderResponse> getOrders(Pageable pageable) {
    return orderService.getOrders(pageable);
}

// Usage: /api/orders?page=0&size=10&sort=orderDate,desc
```

## Common Pitfalls

1. **Page indexing**
   - Pages are zero-based: first page is 0, not 1

2. **Mapping Page content incorrectly**
   - ❌ `new PageImpl<>(list)` (loses pagination metadata)
   - ✅ `orderPage.map(this::mapToResponse)` (preserves metadata)

3. **Sort parameter format**
   - Format: `field,direction` (e.g., "orderDate,desc")
   - Spring automatically parses this

4. **Not setting defaults**
   - Always provide default values for page, size, sort

## Handling Multiple Sort Fields

```java
// For multiple sort fields
Sort sort = Sort.by(
    Sort.Order.desc("orderDate"),
    Sort.Order.asc("id")
);
Pageable pageable = PageRequest.of(page, size, sort);
```

## Testing

```bash
# First page, 10 items, sorted by id
curl "http://localhost:8080/api/orders?page=0&size=10&sort=id,asc"

# Second page, 5 items, sorted by orderDate descending
curl "http://localhost:8080/api/orders?page=1&size=5&sort=orderDate,desc"

# Using Spring's automatic Pageable
curl "http://localhost:8080/api/orders?page=0&size=10&sort=orderDate,desc&sort=id,asc"

mvn test -Dtest=Task07PaginateOrdersTest
```

## Response Structure

```json
{
  "content": [
    { "id": 3, "customerId": 2, ... },
    { "id": 2, "customerId": 1, ... }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": { "sorted": true, "unsorted": false }
  },
  "totalElements": 25,
  "totalPages": 3,
  "last": false,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 10,
  "empty": false
}
```

## Key Points

- `content`: Array of results for current page
- `totalElements`: Total number of items across all pages
- `totalPages`: Total number of pages
- `number`: Current page number (zero-based)
- `size`: Page size
- `first`/`last`: Boolean flags for first/last page
