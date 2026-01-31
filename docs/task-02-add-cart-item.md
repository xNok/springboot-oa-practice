# Task 2: Add Cart Item

## Objective
Implement an endpoint to add a new item to the cart.

## Requirements
Create a POST endpoint that adds a new cart item to the database.

## API Specification
- **HTTP Method**: POST
- **Path**: `/api/cart/items`
- **Request Body**: `CartItemRequest`
- **Response Body**: `CartItemResponse`
- **Success Status**: 201 Created

## Acceptance Criteria
1. Accept a request with productId and quantity
2. Validate that productId and quantity are provided and valid (use @Valid)
3. Validate that the product exists in the database
4. Fetch the product price and name from the Product entity
5. Create a new cart item and save it to the database
6. Return the created cart item with all fields populated
7. Calculate and return the subtotal

## Validation Rules
- productId: required, not null
- quantity: required, positive number
- Product must exist (throw ResourceNotFoundException if not found)

## Error Scenarios
- **400 Bad Request**: Missing or invalid fields (handled by @Valid)
- **404 Not Found**: Product with given ID does not exist

## Implementation Steps
1. Implement the `addCartItem()` method in `CartItemService`
2. Validate that the product exists (use ProductRepository)
3. Fetch product details (price, name)
4. Create a new CartItem entity
5. Save the entity to the repository
6. Map to CartItemResponse and calculate subtotal
7. Implement the POST endpoint in `CartItemController` with @Valid annotation
8. Return with 201 Created status

## Example Request
```json
{
  "productId": 101,
  "quantity": 2
}
```

## Example Response
```json
{
  "id": 3,
  "orderId": null,
  "productId": 101,
  "productName": "Laptop",
  "quantity": 2,
  "price": 999.99,
  "subtotal": 1999.98
}
```

## Expected Time
15-20 minutes

## Testing
Run: `mvn test -Dtest=Task02AddCartItemTest`
