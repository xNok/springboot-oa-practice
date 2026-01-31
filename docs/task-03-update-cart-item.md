# Task 3: Update Cart Item

## Objective
Implement an endpoint to update an existing cart item.

## Requirements
Create a PUT endpoint that updates an existing cart item in the database.

## API Specification
- **HTTP Method**: PUT
- **Path**: `/api/cart/items/{id}`
- **Path Variable**: `id` (Long) - The cart item ID
- **Request Body**: `CartItemRequest`
- **Response Body**: `CartItemResponse`
- **Success Status**: 200 OK

## Acceptance Criteria
1. Accept a cart item ID in the path
2. Accept a request with productId and quantity
3. Validate that the cart item exists
4. Validate that the new product exists (if productId changed)
5. Update the cart item with new values
6. Fetch updated product price and name if product changed
7. Save and return the updated cart item

## Validation Rules
- Cart item with given ID must exist
- productId: required, not null
- quantity: required, positive number
- New product must exist if productId is provided

## Error Scenarios
- **400 Bad Request**: Missing or invalid fields
- **404 Not Found**: Cart item with given ID does not exist
- **404 Not Found**: Product with given ID does not exist

## Implementation Steps
1. Implement the `updateCartItem()` method in `CartItemService`
2. Fetch the existing cart item (throw ResourceNotFoundException if not found)
3. Validate that the new product exists
4. Update the cart item fields (quantity, productId, price, productName)
5. Save the updated entity
6. Map to CartItemResponse and calculate subtotal
7. Implement the PUT endpoint in `CartItemController`

## Example Request
```json
{
  "productId": 102,
  "quantity": 5
}
```

## Example Response
```json
{
  "id": 1,
  "orderId": null,
  "productId": 102,
  "productName": "Mouse",
  "quantity": 5,
  "price": 29.99,
  "subtotal": 149.95
}
```

## Expected Time
15-20 minutes

## Testing
Run: `mvn test -Dtest=Task03UpdateCartItemTest`
