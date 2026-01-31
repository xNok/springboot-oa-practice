# Task 1: Retrieve All Cart Items

## Objective
Implement an endpoint to retrieve all cart items in the system.

## Requirements
Create a GET endpoint that returns all cart items stored in the database.

## API Specification
- **HTTP Method**: GET
- **Path**: `/api/cart-items`
- **Request Body**: None
- **Response Body**: `List<CartItemResponse>`
- **Success Status**: 200 OK

## Acceptance Criteria
1. The endpoint returns a list of all cart items
2. If no cart items exist, return an empty list (not null)
3. Each cart item response includes: id, orderId, productId, productName, quantity, price, and subtotal
4. The subtotal should be calculated as `quantity * price`

## Implementation Steps
1. Implement the `getAllCartItems()` method in `CartItemService`
2. Fetch all cart items from the repository
3. Map CartItem entities to CartItemResponse DTOs
4. Calculate the subtotal for each item
5. Implement the GET endpoint in `CartItemController`
6. Call the service method and return the response

## Example Response
```json
[
  {
    "id": 1,
    "orderId": 1,
    "productId": 101,
    "productName": "Laptop",
    "quantity": 2,
    "price": 999.99,
    "subtotal": 1999.98
  },
  {
    "id": 2,
    "orderId": 1,
    "productId": 102,
    "productName": "Mouse",
    "quantity": 1,
    "price": 29.99,
    "subtotal": 29.99
  }
]
```

## Expected Time
10-15 minutes

## Testing
Run: `mvn test -Dtest=Task01GetAllCartItemsTest`
