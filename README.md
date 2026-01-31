# Spring Boot Online Assessment Practice

A comprehensive practice project for Spring Boot online assessments, focusing on building a REST API for an e-commerce order management system.

## 📋 Overview

This project provides a skeleton Spring Boot application with 12 progressive tasks covering:
- **CRUD Operations** (Tasks 1-6): Basic create, read, update, delete operations
- **Filtering & Pagination** (Tasks 7-9): Advanced querying with Spring Data JPA
- **State Management** (Tasks 10-12): Order status transitions with business rules

The project includes:
- ✅ Complete skeleton application with entities, DTOs, and error handling
- ✅ Task documentation with requirements and acceptance criteria
- ✅ Solution hints with code examples and best practices
- ✅ Comprehensive test suite to validate implementations
- ✅ Test data for realistic scenarios

## 🎯 Learning Objectives

By completing these tasks, you will practice:
- Spring Boot REST API development
- Spring Data JPA repository patterns
- Request/response validation with Bean Validation
- Error handling and exception mapping
- Pagination and filtering
- State machine implementation
- Test-driven development with MockMvc

## 🏗️ Architecture

```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Spring Data JPA)
    ↓
H2 Database (In-Memory)
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- IDE (VS Code, IntelliJ IDEA, or Eclipse)

### Setup

1. **Clone or open the repository**
```bash
cd /workspaces/springboot-oa-practice
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

4. **Import OpenAPI spec into Postman (recommended)**
- Import the `openapi.yaml` file from the root directory into Postman
- This provides a complete collection of all API endpoints with examples
- Makes testing your implementation much easier!

5. **Access H2 Console (for debugging)**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## 📚 Task Progression

### Phase 1: CRUD Operations (40-50 minutes)

| Task | Description | Estimated Time |
|------|-------------|----------------|
| [Task 1](docs/task-01-get-all-cart-items.md) | GET all cart items | 10-15 min |
| [Task 2](docs/task-02-add-cart-item.md) | POST create cart item | 15-20 min |
| [Task 3](docs/task-03-update-cart-item.md) | PUT update cart item | 15-20 min |
| [Task 4](docs/task-04-delete-cart-item.md) | DELETE cart item | 10 min |
| [Task 5](docs/task-05-create-order.md) | POST create order | 10-15 min |
| [Task 6](docs/task-06-get-order-by-id.md) | GET order by ID | 10 min |

### Phase 2: Filtering & Pagination (35-40 minutes)

| Task | Description | Estimated Time |
|------|-------------|----------------|
| [Task 7](docs/task-07-paginate-orders.md) | Implement pagination | 15-20 min |
| [Task 8](docs/task-08-filter-orders-by-status.md) | Filter by status | 15 min |
| [Task 9](docs/task-09-filter-orders-by-date-range.md) | Filter by date range | 20 min |

### Phase 3: State Management (35-40 minutes)

| Task | Description | Estimated Time |
|------|-------------|----------------|
| [Task 10](docs/task-10-update-order-status.md) | Update order status | 20-25 min |
| [Task 11](docs/task-11-validate-state-transitions.md) | Validate transitions | (integrated with Task 10) |
| [Task 12](docs/task-12-cancel-order.md) | Cancel order endpoint | 10-15 min |

### Phase 4: Integration (BONUS) (25-30 minutes)

| Task | Description | Estimated Time |
|------|-------------|----------------|
| [Task 13](docs/task-13-checkout-create-order-from-cart.md) | Checkout - Create order from cart | 25-30 min |

**Total Estimated Time: 2-2.5 hours (2.5-3 hours with bonus task)**

## 🧪 Testing Your Implementation

### Run All Tests
```bash
mvn test
```

### Run Tests for Specific Task
```bash
# Example: Test Task 1
mvn test -Dtest=Task01GetAllCartItemsTest

# Example: Test Task 5
mvn test -Dtest=Task05CreateOrderTest
```

### Run Tests by Phase
```bash
# CRUD tests (Tasks 1-6)
mvn test -Dtest=Task0*Test

# State management tests (Tasks 10-12)
mvn test -Dtest=Task1*Test
```

### Check Test Results
Tests will initially **FAIL** because the methods throw `UnsupportedOperationException`.
As you implement each task, the corresponding tests should **PASS**.

## 🔧 What's Provided

### Already Implemented (Infrastructure)
- ✅ Maven project configuration with dependencies
- ✅ Spring Boot application entry point
- ✅ Entity models (Order, CartItem, Product, Customer) with JPA annotations
- ✅ Repository interfaces (OrderRepository, CartItemRepository)
- ✅ Request/Response DTOs with validation annotations
- ✅ Global exception handler (@ControllerAdvice)
- ✅ Custom exception classes
- ✅ Empty service classes with TODO methods
- ✅ Empty controller classes with task comments
- ✅ Test suite with test data
- ✅ H2 database configuration

### You Need to Implement
- ❌ Service layer business logic
- ❌ Controller endpoint implementations
- ❌ Repository query methods (for filtering)
- ❌ Entity-to-DTO mapping logic
- ❌ State transition validation
- ❌ Exception throwing for error cases

## 📖 How to Use This Project

1. **Read the task documentation** in the `docs/` folder
2. **Implement the required functionality** in service and controller classes
3. **Run the tests** to validate your implementation
4. **If stuck, check the hints** in `solutions/task-XX/hint.md` (but try yourself first!)
5. **Move to the next task** once tests pass

## 🎓 Solution Hints

Solution hints are available in the `solutions/` folder:
- `solutions/task-01/hint.md` through `solutions/task-12/hint.md`
- Each hint includes:
  - Implementation approach
  - Code examples
  - Common pitfalls
  - Alternative implementations
  - Testing instructions

**Recommendation:** Try to implement each task yourself before checking the hints!

## 🗂️ Project Structure

```
springboot-oa-practice/
├── docs/                          # Task documentation
│   ├── task-01-get-all-cart-items.md
│   ├── task-02-add-cart-item.md
│   └── ... (tasks 3-12)
├── solutions/                     # Solution hints
│   ├── task-01/hint.md
│   ├── task-02/hint.md
│   └── ... (tasks 3-12)
├── openapi.yaml                   # OpenAPI 3.0 spec (import into Postman!)
├── src/
│   ├── main/
│   │   ├── java/com/example/oa/
│   │   │   ├── Application.java
│   │   │   ├── controller/       # REST controllers (implement here)
│   │   │   ├── service/          # Business logic (implement here)
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── dto/              # Request/Response DTOs
│   │   │   └── exception/        # Exception handling
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/example/oa/controller/
│       │   ├── Task01GetAllCartItemsTest.java
│       │   └── ... (tests for all tasks)
│       └── resources/test-data/
│           ├── base-data.sql      # Products and customers
│           ├── cart-items.sql     # Test cart items
│           └── orders.sql         # Test orders
├── pom.xml
└── README.md
```

## 🔍 Key Endpoints to Implement

### CartItem Endpoints
- `GET /api/cart/items` - Get all cart items
- `POST /api/cart/items` - Add new cart item
- `PUT /api/cart/items/{id}` - Update cart item
- `DELETE /api/cart/items/{id}` - Delete cart item

### Order Endpoints
- `POST /api/orders` - Create new order
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders?page=0&size=10&sort=id` - Get orders with pagination
- `GET /api/orders?status=CONFIRMED` - Filter by status
- `GET /api/orders?startDate=...&endDate=...` - Filter by date range
- `PATCH /api/orders/{id}/status` - Update order status
- `POST /api/orders/{id}/cancel` - Cancel order

## 🎨 Order Status State Machine

```
CREATED ──────> CONFIRMED ──────> SHIPPED ──────> DELIVERED
   │               │                  │
   │               │                  │
   └───────────────┴──────────────────┴──────> CANCELLED
```

**Rules:**
- DELIVERED and CANCELLED are final states
- Cannot transition backward (e.g., SHIPPED → CONFIRMED)
- Cannot transition to CREATED from any state

## 🧰 Technologies Used

- **Spring Boot 3.2.2** - Application framework
- **Spring Data JPA** - Data access layer
- **Spring Web** - REST API
- **Spring Validation** - Request validation
- **H2 Database** - In-memory database
- **Lombok** - Reduce boilerplate code
- **JUnit 5** - Testing framework
- **MockMvc** - REST API testing

## 🧪 API Testing with Postman

### Import OpenAPI Spec
1. Open Postman
2. Click "Import" button
3. Select the `openapi.yaml` file from the project root
4. Postman will create a collection with all endpoints
5. Start testing your implementation!

### Manual Testing with curl
```bash
# Get all cart items
curl http://localhost:8080/api/cart/items

# Add a cart item
curl -X POST http://localhost:8080/api/cart/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'

# Get orders with pagination
curl "http://localhost:8080/api/orders?page=0&size=10&sort=orderDate,desc"

# Update order status
curl -X PATCH http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'
```

## 💡 Tips for Success

1. **Use Postman with the OpenAPI spec** - Import `openapi.yaml` for easy testing
2. **Read the task requirements carefully** - Understand acceptance criteria before coding
2. **Start with simple tasks** - Tasks 1 and 6 are good starting points
3. **Run tests frequently** - Get immediate feedback on your implementation
4. **Use the provided infrastructure** - Don't rewrite exception handling or DTOs
5. **Think about edge cases** - What if entity doesn't exist? What if validation fails?
6. **Check logs** - Enable `spring.jpa.show-sql=true` to see SQL queries
7. **Use H2 console** - Inspect database state during development

## 🐛 Common Pitfalls to Avoid

- ❌ Not throwing ResourceNotFoundException when entity not found
- ❌ Forgetting @Valid annotation on request parameters
- ❌ Not mapping entities to DTOs (returning entities directly)
- ❌ Using wrong HTTP status codes (e.g., 200 instead of 201 for POST)
- ❌ Not calculating subtotal for cart items
- ❌ Not setting default values (orderDate, initial status)
- ❌ Forgetting to inject repositories with @Autowired

## 📝 Assessment Criteria

Your implementation will be evaluated on:
- ✅ **Correctness** - All tests pass
- ✅ **Code Quality** - Clean, readable, idiomatic Spring Boot code
- ✅ **Error Handling** - Proper exception throwing and HTTP status codes
- ✅ **Validation** - Using Bean Validation annotations
- ✅ **Best Practices** - Following Spring Boot conventions

## 🤝 Contributing

This is a practice project for learning. Feel free to:
- Add more tasks
- Improve test coverage
- Add integration tests
- Extend with additional features (authentication, etc.)

## 📄 License

This project is provided under the MIT License for educational purposes.

## 🆘 Getting Help

1. **Read the task documentation** in `docs/`
2. **Check solution hints** in `solutions/`
3. **Review test expectations** in test classes
4. **Consult Spring Boot documentation**: https://spring.io/projects/spring-boot

---

**Good luck with your practice! 🚀**