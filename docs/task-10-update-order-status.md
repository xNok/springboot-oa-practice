# Task 10: Update Order Status

## Objective
Implement an endpoint to update the status of an order with validation.

## Requirements
Create a PATCH endpoint that updates an order's status with state transition validation.

## API Specification
- **HTTP Method**: PATCH
- **Path**: `/api/orders/{id}/status`
- **Path Variable**: `id` (Long) - The order ID
- **Request Body**: `UpdateOrderStatusRequest` (contains new status)
- **Response Body**: `OrderResponse`
- **Success Status**: 200 OK

## Acceptance Criteria
1. Accept an order ID in the path
2. Accept a new status in the request body
3. Validate that the order exists
4. Validate that the status transition is valid (see state transition rules)
5. Update the order status if valid
6. Return the updated order

## State Transition Rules
**Valid transitions:**
- CREATED → CONFIRMED
- CONFIRMED → SHIPPED
- SHIPPED → DELIVERED
- CREATED → CANCELLED
- CONFIRMED → CANCELLED
- SHIPPED → CANCELLED (edge case, e.g., lost package)

**Invalid transitions:**
- DELIVERED → any state (final state)
- Backward transitions (e.g., SHIPPED → CONFIRMED)
- Any state → CREATED (cannot reset)

## Error Scenarios
- **404 Not Found**: Order with given ID does not exist
- **422 Unprocessable Entity**: Invalid state transition
- **400 Bad Request**: Missing or invalid status in request

## Implementation Steps
1. Implement the `updateOrderStatus()` method in `OrderService`
2. Fetch the order (throw ResourceNotFoundException if not found)
3. Validate the state transition:
   - Create a helper method `isValidTransition(OrderStatus from, OrderStatus to)`
   - If invalid, throw BusinessRuleException with descriptive message
4. Update the order status
5. Save and return the updated order
6. Implement the PATCH endpoint in `OrderController`

## Example Request
```json
{
  "status": "CONFIRMED"
}
```

## Example Response (Success)
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

## Example Error Response (422)
```json
{
  "message": "Invalid state transition from DELIVERED to CREATED",
  "status": 422,
  "timestamp": "2026-01-31T11:00:00",
  "errors": []
}
```

## Tips
- Use a switch statement or map for transition validation
- Provide clear error messages indicating the invalid transition
- Consider creating an enum or matrix for valid transitions

## Expected Time
20-25 minutes

## Testing
Run: `mvn test -Dtest=Task10UpdateOrderStatusTest`
