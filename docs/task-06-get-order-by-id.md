# Task 6: Get Order by ID

## Objective
Implement an endpoint to retrieve a specific order by its ID.

## Requirements
Create a GET endpoint that retrieves a single order from the database.

## API Specification
- **HTTP Method**: GET
- **Path**: `/api/orders/{id}`
- **Path Variable**: `id` (Long) - The order ID
- **Request Body**: None
- **Response Body**: `OrderResponse`
- **Success Status**: 200 OK

## Acceptance Criteria
1. Accept an order ID in the path
2. Fetch the order from the database
3. If the order exists, return it with all fields populated
4. If the order does not exist, throw ResourceNotFoundException

## Validation Rules
- Order with given ID must exist

## Error Scenarios
- **404 Not Found**: Order with given ID does not exist

## Implementation Steps
1. Implement the `getOrderById()` method in `OrderService`
2. Use orderRepository.findById(id)
3. If not found, throw new ResourceNotFoundException("Order", id)
4. Map the Order entity to OrderResponse
5. Implement the GET endpoint in `OrderController`

## Example Request
```
GET /api/orders/1
```

## Example Response
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

## Example Error Response (404)
```json
{
  "message": "Order not found with id: 999",
  "status": 404,
  "timestamp": "2026-01-31T10:35:00",
  "errors": []
}
```

## Expected Time
10 minutes

## Testing
Run: `mvn test -Dtest=Task06GetOrderByIdTest`
