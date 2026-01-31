# Task 4: Delete Cart Item

## Objective
Implement an endpoint to delete a cart item from the database.

## Requirements
Create a DELETE endpoint that removes a cart item from the database.

## API Specification
- **HTTP Method**: DELETE
- **Path**: `/api/cart-items/{id}`
- **Path Variable**: `id` (Long) - The cart item ID
- **Request Body**: None
- **Response Body**: None
- **Success Status**: 204 No Content

## Acceptance Criteria
1. Accept a cart item ID in the path
2. Validate that the cart item exists
3. Delete the cart item from the database
4. Return no content (empty body) with 204 status

## Validation Rules
- Cart item with given ID must exist

## Error Scenarios
- **404 Not Found**: Cart item with given ID does not exist

## Implementation Steps
1. Implement the `deleteCartItem()` method in `CartItemService`
2. Check if the cart item exists (throw ResourceNotFoundException if not found)
3. Delete the cart item from the repository
4. Implement the DELETE endpoint in `CartItemController`
5. Use @ResponseStatus(HttpStatus.NO_CONTENT) or return ResponseEntity<Void>

## Example Request
```
DELETE /api/cart-items/1
```

## Example Response
```
HTTP/1.1 204 No Content
```

## Expected Time
10 minutes

## Testing
Run: `mvn test -Dtest=Task04DeleteCartItemTest`
