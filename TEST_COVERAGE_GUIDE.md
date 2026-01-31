# Test Coverage Guide

This document provides a clear mapping of which test files cover which tasks, making it easy for candidates to understand test coverage and run tests for specific tasks.

## Test Coverage Summary

| Task # | Task Name | Test File | Status |
|--------|-----------|-----------|--------|
| 01 | Get All Cart Items | `Task01GetAllCartItemsTest.java` | ✅ |
| 02 | Add Cart Item | `Task02AddCartItemTest.java` | ✅ |
| 03 | Update Cart Item | `Task03UpdateCartItemTest.java` | ✅ |
| 04 | Delete Cart Item | `Task04DeleteCartItemTest.java` | ✅ |
| 05 | Create Order | `Task05CreateOrderTest.java` | ✅ |
| 06 | Get Order by ID | `Task06GetOrderByIdTest.java` | ✅ |
| 07 | Paginate Orders | `Task07PaginateOrdersTest.java` | ✅ |
| 08 | Filter Orders by Status | `Task08FilterOrdersByStatusTest.java` | ✅ |
| 09 | Filter Orders by Date Range | `Task09FilterOrdersByDateRangeTest.java` | ✅ |
| 10 | Update Order Status | `Task10UpdateOrderStatusTest.java` | ✅ |
| 11 | Validate State Transitions | `Task11ValidateStateTransitionsTest.java` | ✅ |
| 12 | Cancel Order | `Task12CancelOrderTest.java` | ✅ |

## Running Tests

### Run all tests
```bash
mvn test
```

### Run a specific task test
```bash
# Example: Run Task 1 test
mvn test -Dtest=Task01GetAllCartItemsTest

# Example: Run Task 3 test
mvn test -Dtest=Task03UpdateCartItemTest
```

### Run all task tests matching a pattern
```bash
# Run all cart item tests (Tasks 1-4)
mvn test -Dtest=Task0[1-4].*

# Run all order tests (Tasks 5-12)
mvn test -Dtest=Task(05|06|07|08|09|10|11|12).*
```

## Test Organization

### Cart Item Tests (Tasks 1-4)
Tests for shopping cart functionality:
- **Task 01 - Get All Cart Items**: Tests retrieving all items in the cart
- **Task 02 - Add Cart Item**: Tests adding new items to the cart with validation
- **Task 03 - Update Cart Item**: Tests modifying existing cart items
- **Task 04 - Delete Cart Item**: Tests removing items from the cart

### Order Tests (Tasks 5-10)
Tests for order management functionality:
- **Task 05 - Create Order**: Tests order creation from cart items
- **Task 06 - Get Order by ID**: Tests retrieving specific orders
- **Task 07 - Paginate Orders**: Tests pagination support for order listings
- **Task 08 - Filter Orders by Status**: Tests filtering orders by status (PENDING, SHIPPED, etc.)

### Advanced Order Tests (Tasks 9-12)
Tests for complex order operations:
- **Task 09 - Filter Orders by Date Range**: Tests date-range filtering with various date combinations
- **Task 10 - Update Order Status**: Tests basic order status updates
- **Task 11 - Validate State Transitions**: Tests business rules for valid order status transitions
- **Task 12 - Cancel Order**: Tests order cancellation with state validation

## Test Naming Convention

All test files follow the naming pattern: `Task{NUMBER}{Name}Test.java`

Examples:
- `Task01GetAllCartItemsTest.java` - Tests for Task 1
- `Task07PaginateOrdersTest.java` - Tests for Task 7
- `Task12CancelOrderTest.java` - Tests for Task 12

## What Each Test Validates

Each test file includes:
- **Javadoc comment**: Clear description of what the test validates
- **Multiple test methods**: Various scenarios (success cases, validation errors, edge cases)
- **Setup/Teardown**: Base data loaded before tests and cleaned up after

### Example Test Structure
```java
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TaskXXTestNameTest {
    // Multiple test methods covering different scenarios
    @Test
    public void testXXX_Success() { ... }
    
    @Test
    public void testXXX_ValidationError() { ... }
    
    @Test
    public void testXXX_NotFound() { ... }
}
```

## Finding Tests

All test files are located in: `/src/test/java/com/example/oa/controller/`

Use your IDE's file search (Ctrl+P or Cmd+P) to quickly navigate to any test:
- Search: `Task01` → Find Task01GetAllCartItemsTest.java
- Search: `Task03` → Find Task03UpdateCartItemTest.java

## Test Data

Tests use pre-configured test data:
- `base-data.sql`: Common base data (products, customers)
- `cart-items.sql`: Sample cart items
- `orders.sql`: Sample orders with various statuses
- `cleanup.sql`: Cleans up after each test run

This ensures tests are isolated and reproducible.
