# Task 5: Create Order

## Objective
Implement an endpoint to create a new order.

## Requirements
Create a POST endpoint that creates a new order in the database.

## API Specification
- **HTTP Method**: POST
- **Path**: `/api/orders`
- **Request Body**: `OrderRequest`
- **Response Body**: `OrderResponse`
- **Success Status**: 201 Created

## Acceptance Criteria
1. Accept a request with customerId, customerName, and totalAmount
2. Validate that required fields are provided
3. Set the order date to the current timestamp
4. Set the initial status to CREATED
5. Save the order to the database
6. Return the created order with all fields populated

## Validation Rules
- customerId: required, not null
- totalAmount: required, positive number
- customerName: optional

## Error Scenarios
- **400 Bad Request**: Missing or invalid required fields

## Implementation Steps
1. Implement the `createOrder()` method in `OrderService`
2. Create a new Order entity from the request
3. Set orderDate to LocalDateTime.now()
4. Set status to OrderStatus.CREATED
5. Save the entity to the repository
6. Map to OrderResponse and return
7. Implement the POST endpoint in `OrderController` with @Valid annotation
8. Return with 201 Created status

## Example Request
```json
{
  "customerId": 1,
  "customerName": "John Doe",
  "totalAmount": 2029.97
}
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

## Expected Time
10-15 minutes

## Testing
Run: `mvn test -Dtest=Task05CreateOrderTest`
