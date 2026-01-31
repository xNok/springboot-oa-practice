# Task 9: Filter Orders by Date Range

## Objective
Implement filtering to retrieve orders within a specific date range.

## Requirements
Add filtering capability to retrieve orders created between two dates.

## API Specification
- **HTTP Method**: GET
- **Path**: `/api/orders`
- **Query Parameters**:
  - `startDate` (LocalDateTime, optional) - Start of date range (inclusive)
  - `endDate` (LocalDateTime, optional) - End of date range (inclusive)
  - `page` (int, default: 0)
  - `size` (int, default: 10)
  - `sort` (string, default: "id")
- **Response Body**: `Page<OrderResponse>`
- **Success Status**: 200 OK

## Acceptance Criteria
1. Support filtering by startDate and endDate query parameters
2. If both dates provided, return orders where orderDate is between them (inclusive)
3. If only startDate provided, return orders on or after that date
4. If only endDate provided, return orders on or before that date
5. If neither provided, return all orders
6. Dates should be in ISO format: `yyyy-MM-ddTHH:mm:ss`
7. Maintain pagination and sorting support

## Implementation Steps
1. Add a custom query method to OrderRepository:
   - `Page<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable)`
2. Optionally add methods for single-bound filtering:
   - `findByOrderDateAfter()`, `findByOrderDateBefore()`
3. Implement the `getOrdersByDateRange()` method in `OrderService`
4. Handle different combinations of start/end dates
5. Update the GET endpoint in `OrderController`
6. Use `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)` for parameter binding

## Example Request
```
GET /api/orders?startDate=2026-01-01T00:00:00&endDate=2026-01-31T23:59:59&page=0&size=10
```

## Example Response
```json
{
  "content": [
    {
      "id": 1,
      "customerId": 1,
      "customerName": "John Doe",
      "orderDate": "2026-01-15T10:30:00",
      "status": "DELIVERED",
      "totalAmount": 2029.97
    },
    {
      "id": 2,
      "customerId": 2,
      "customerName": "Jane Smith",
      "orderDate": "2026-01-20T14:00:00",
      "status": "SHIPPED",
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
- Use `@RequestParam(required = false)` for optional parameters
- Use `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)` annotation
- Handle cases where only one date is provided
- Consider using Specification API for more complex filtering

## Expected Time
20 minutes

## Testing
Run: `mvn test -Dtest=Task09FilterOrdersByDateRangeTest`
