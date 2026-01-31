# Task 7: Paginate Orders

## Objective
Implement pagination support for the orders endpoint.

## Requirements
Enhance the GET orders endpoint to support pagination and sorting.

## API Specification
- **HTTP Method**: GET
- **Path**: `/api/orders`
- **Query Parameters**:
  - `page` (int, default: 0) - Page number (zero-based)
  - `size` (int, default: 10) - Number of items per page
  - `sort` (string, default: "id") - Sort field and direction (e.g., "orderDate,desc")
- **Response Body**: `Page<OrderResponse>`
- **Success Status**: 200 OK

## Acceptance Criteria
1. Support page, size, and sort query parameters
2. Return a Page object with content, pagination metadata
3. Default values: page=0, size=10, sort by id ascending
4. Support sorting by any order field (id, orderDate, totalAmount, status)
5. Support sort direction (asc/desc)

## Implementation Steps
1. Implement the `getOrders()` method in `OrderService`
2. Accept a Pageable parameter
3. Use orderRepository.findAll(pageable)
4. Map Page<Order> to Page<OrderResponse>
5. Implement the GET endpoint in `OrderController`
6. Use PageRequest.of() to create Pageable from query params
7. Return the Page object

## Example Request
```
GET /api/orders?page=0&size=5&sort=orderDate,desc
```

## Example Response
```json
{
  "content": [
    {
      "id": 3,
      "customerId": 2,
      "customerName": "Jane Smith",
      "orderDate": "2026-01-31T14:00:00",
      "status": "CONFIRMED",
      "totalAmount": 599.99
    },
    {
      "id": 2,
      "customerId": 1,
      "customerName": "John Doe",
      "orderDate": "2026-01-31T12:00:00",
      "status": "CREATED",
      "totalAmount": 1299.99
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 12,
  "totalPages": 3,
  "last": false,
  "first": true,
  "size": 5,
  "number": 0
}
```

## Tips
- Use Spring Data's `Pageable` interface
- Use `PageRequest.of(page, size, Sort.by(sortBy).descending())` for descending sort
- Handle sort parameter parsing (e.g., "orderDate,desc" → field + direction)

## Expected Time
15-20 minutes

## Testing
Run: `mvn test -Dtest=Task07PaginateOrdersTest`
