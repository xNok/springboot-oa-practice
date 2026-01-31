# Task 13: Checkout - Create Order from Cart (BONUS)

## Objective
Implement a checkout endpoint that creates an order from current cart items.

## Requirements
Create a POST endpoint that converts cart items into an order, linking them together and calculating the total automatically.

## API Specification
- **HTTP Method**: POST
- **Path**: `/api/cart/checkout`
- **Request Body**: `CheckoutRequest`
- **Response Body**: `OrderResponse`
- **Success Status**: 201 Created

## Acceptance Criteria
1. Accept a request with customerId and customerName
2. Fetch all cart items that don't have an orderId (items in cart, not yet ordered)
3. Validate that there are cart items to checkout
4. Calculate the total amount from all cart items (sum of subtotals)
5. Create a new order with calculated total
6. Set orderDate to current timestamp and status to CREATED
7. Link all cart items to the created order (set their orderId)
8. Save the order and update cart items
9. Return the created order

## Validation Rules
- customerId: required, not null
- Cart must have at least one item (throw BusinessRuleException if empty)
- Cart items must have valid product references

## Error Scenarios
- **400 Bad Request**: Missing or invalid required fields
- **422 Unprocessable Entity**: Cart is empty, cannot checkout

## Implementation Steps
1. Create `CheckoutRequest` DTO with customerId and customerName
2. Add `checkout()` method to `CartItemService` or `OrderService`
3. Fetch all cart items where orderId is null
4. Validate cart is not empty
5. Calculate total: sum of (quantity × price) for all items
6. Create Order entity with calculated total
7. Save the order
8. Update all cart items with the new orderId
9. Save updated cart items
10. Return OrderResponse

## Example Request
```json
{
  "customerId": 1,
  "customerName": "John Doe"
}
```

## Example Response
```json
{
  "id": 10,
  "customerId": 1,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T15:30:00",
  "status": "CREATED",
  "totalAmount": 2109.96
}
```

## Business Logic
- **Cart items become order items**: Once checkout is complete, cart items are linked to the order
- **Total is calculated**: No manual total entry needed
- **Atomic operation**: Either all items are checked out or none
- **Cart state after checkout**: Items have orderId set, so they won't appear in active cart

## Additional Considerations
1. **Transaction management**: Use `@Transactional` to ensure atomicity
2. **Concurrency**: Consider what happens if cart is modified during checkout
3. **Empty cart validation**: Prevent checkout with no items
4. **Partial checkout**: Current design checks out ALL cart items (could extend to support selected items)

## Extension Ideas (Not Required)
- Add ability to specify which cart item IDs to checkout (partial checkout)
- Add ability to apply discounts or coupons
- Validate product availability/stock before checkout
- Send confirmation email after successful checkout

## Expected Time
25-30 minutes

## Testing
Run: `mvn test -Dtest=Task13CheckoutTest`

## Integration with Other Tasks
- **Builds on Task 1**: Uses getAllCartItems logic
- **Builds on Task 5**: Uses createOrder logic
- **Requires understanding**: CartItem-Order relationship
- **State management**: Cart items transition from "in cart" to "in order"
