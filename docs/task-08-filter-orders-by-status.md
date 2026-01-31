# Task 8: Filter Orders by Status

## Objective
Implement filtering to retrieve orders by status with pagination.

## Requirements
Add filtering capability to the orders endpoint to filter by order status.

## API Specification
- **HTTP Method**: GET
- **Path**: `/api/orders`
- **Query Parameters**:
  - `status` (OrderStatus, optional) - Filter by order status
  - `page` (int, default: 0)
  - `size` (int, default: 10)
  - `sort` (string, default: "id")
- **Response Body**: `Page<OrderResponse>`
- **Success Status**: 200 OK

## Acceptance Criteria
1. Support filtering by status query parameter
2. If status is provided, return only orders with that status
3. If status is not provided, return all orders
4. Maintain pagination and sorting support
5. Status values: CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

## Implementation Steps
1. Add a custom query method to OrderRepository:
   - `Page<Order> findByStatus(OrderStatus status, Pageable pageable)`
2. Implement the `getOrdersByStatus()` method in `OrderService`
3. Use the repository method with status filter and pageable
4. Map results to Page<OrderResponse>
5. Update the GET endpoint in `OrderController` to accept status parameter
6. Call the appropriate service method based on whether status is provided

## Example Request
```
GET /api/orders?status=CONFIRMED&page=0&size=10&sort=orderDate,desc
```

## Example Response
```json
{
  "content": [
    {
      "id": 5,
      "customerId": 3,
      "customerName": "Alice Johnson",
      "orderDate": "2026-01-31T15:00:00",
      "status": "CONFIRMED",
      "totalAmount": 1599.99
    },
    {
      "id": 3,
      "customerId": 2,
      "customerName": "Jane Smith",
      "orderDate": "2026-01-31T14:00:00",
      "status": "CONFIRMED",
      "totalAmount": 599.99
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

## Tips
- Add the query method to OrderRepository interface
- Spring Data JPA will automatically implement it
- Use `@RequestParam(required = false)` for optional status parameter
- Consider using Optional<OrderStatus> or checking for null

## Expected Time
15 minutes

## Testing
Run: `mvn test -Dtest=Task08FilterOrdersByStatusTest`
