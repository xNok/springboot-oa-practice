# Task 12: Cancel Order

## Objective
Implement a dedicated endpoint to cancel an order with business rule validation.

## Requirements
Create a POST endpoint that cancels an order if it's in a cancellable state.

## API Specification
- **HTTP Method**: POST
- **Path**: `/api/orders/{id}/cancel`
- **Path Variable**: `id` (Long) - The order ID
- **Request Body**: None
- **Response Body**: `OrderResponse`
- **Success Status**: 200 OK

## Acceptance Criteria
1. Accept an order ID in the path
2. Validate that the order exists
3. Validate that the order can be cancelled (not DELIVERED or already CANCELLED)
4. Update the order status to CANCELLED
5. Return the updated order

## Business Rules for Cancellation
**Can be cancelled:**
- CREATED → CANCELLED
- CONFIRMED → CANCELLED
- SHIPPED → CANCELLED (edge case: lost package, customer return)

**Cannot be cancelled:**
- DELIVERED orders (final state, order completed)
- Already CANCELLED orders (idempotent, but can handle gracefully)

## Error Scenarios
- **404 Not Found**: Order with given ID does not exist
- **422 Unprocessable Entity**: Order cannot be cancelled (already DELIVERED)
- **422 Unprocessable Entity**: Order is already cancelled (optional: could return 200 OK)

## Implementation Steps
1. Implement the `cancelOrder()` method in `OrderService`
2. Fetch the order (throw ResourceNotFoundException if not found)
3. Check current status:
   - If DELIVERED, throw BusinessRuleException ("Cannot cancel a delivered order")
   - If already CANCELLED, either:
     - Return the order as-is (idempotent operation), or
     - Throw BusinessRuleException ("Order is already cancelled")
4. Update status to CANCELLED
5. Save and return the updated order
6. Implement the POST endpoint in `OrderController`

## Example Request
```
POST /api/orders/1/cancel
```

## Example Response (Success)
```json
{
  "id": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T10:30:00",
  "status": "CANCELLED",
  "totalAmount": 2029.97
}
```

## Example Error Response (422 - Cannot Cancel)
```json
{
  "message": "Cannot cancel a delivered order",
  "status": 422,
  "timestamp": "2026-01-31T11:00:00",
  "errors": []
}
```

## Tips
- This is a convenience method that wraps updateOrderStatus
- You can reuse the validation logic from Task 10/11
- Consider making it idempotent (cancelling an already cancelled order returns success)
- Use a dedicated endpoint for better API semantics

## Expected Time
10-15 minutes

## Testing
Run: `mvn test -Dtest=Task12CancelOrderTest`
