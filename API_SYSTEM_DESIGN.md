# E-Commerce Order Management API - System Design

> **Interview-Style System Design Document**  
> Design a scalable REST API for an e-commerce order management system with shopping cart functionality

---

## Table of Contents
1. [Problem Statement](#problem-statement)
2. [Requirements](#requirements)
3. [Assumptions & Constraints](#assumptions--constraints)
4. [Core Entities & Data Model](#core-entities--data-model)
5. [High-Level Architecture](#high-level-architecture)
6. [API Design](#api-design)
7. [Database Schema](#database-schema)
8. [Deep Dive: Core Challenges](#deep-dive-core-challenges)
9. [Trade-offs & Alternatives](#trade-offs--alternatives)
10. [Scalability & Performance](#scalability--performance)
11. [Security Considerations](#security-considerations)
12. [Migration Path](#migration-path)

---

## Problem Statement

Design a RESTful API for an e-commerce platform that supports:
- **Shopping Cart Management**: Customers can add, update, view, and remove items from their cart
- **Order Management**: Customers can create orders, view order history, and track order status
- **Order State Transitions**: Orders progress through states (CREATED → CONFIRMED → SHIPPED → DELIVERED)
- **Checkout Flow**: Convert shopping cart to order with automatic total calculation

**Key Challenge**: Current implementation lacks customer context, causing security vulnerabilities and unclear resource ownership.

### Current Problems

```
❌ POST /api/cart/items { "productId": 1, "quantity": 2 }
   └─ Which customer's cart? UNKNOWN!
   
❌ GET /api/cart/items
   └─ Returns ALL customers' carts (security violation)
   
❌ POST /api/orders { "customerId": 1, "totalAmount": 99.99 }
   └─ Manual total? Where do items come from?
```

---

## Requirements

### Functional Requirements

#### Shopping Cart (FR1-FR4)
- **FR1**: Customers can view all items in their cart
- **FR2**: Customers can add items to their cart (with quantity)
- **FR3**: Customers can update item quantities in their cart
- **FR4**: Customers can remove items from their cart

#### Order Management (FR5-FR9)
- **FR5**: Customers can create orders from their cart (checkout)
- **FR6**: Customers can view specific orders by ID
- **FR7**: Customers can list their orders with pagination
- **FR8**: Customers can filter orders by status (CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- **FR9**: Customers can filter orders by date range

#### Order State Management (FR10-FR12)
- **FR10**: System can update order status following valid state transitions
- **FR11**: System validates state transitions (e.g., can't ship a cancelled order)
- **FR12**: Customers can cancel orders (with business rules)

#### Checkout Flow (FR13)
- **FR13**: Checkout automatically converts cart items to order, calculates total, and clears cart

### Non-Functional Requirements

- **NFR1 - Security**: Customer data isolation (can't access other customers' carts/orders)
- **NFR2 - Performance**: Support 10K concurrent users, < 200ms response time
- **NFR3 - Scalability**: Horizontally scalable (stateless services)
- **NFR4 - Availability**: 99.9% uptime
- **NFR5 - Data Integrity**: ACID transactions for checkout (cart → order conversion)
- **NFR6 - RESTful**: Follow REST principles (proper resource hierarchy, HTTP verbs)

### Out of Scope
- Payment processing
- Inventory management
- Product catalog management
- User authentication (assume handled by API gateway)
- Shipping integration
- Real-time notifications

---

## Assumptions & Constraints

### Assumptions
1. **Authentication exists upstream**: API gateway validates customer identity and passes `customerId` in requests
2. **Product data is provided**: Product service provides product details (name, price)
3. **Customer data is provided**: Customer service provides customer details (name, email)
4. **Single currency**: All prices in USD
5. **Single warehouse**: No multi-location inventory
6. **Synchronous operations**: No async processing for order creation (< 1 second)
7. **Read-heavy workload**: 80% reads (view cart/orders), 20% writes (add to cart, checkout)

### Constraints
1. **Technology stack**: Spring Boot 3.x, JPA, H2/PostgreSQL, REST
2. **Data consistency**: Strong consistency for checkout (ACID transactions)
3. **Response time**: < 200ms for 95th percentile
4. **Concurrent checkouts**: Support 100 concurrent checkouts/second
5. **Order history retention**: 5 years minimum
6. **Cart lifespan**: 30 days (abandoned cart cleanup)

---

## Core Entities & Data Model

### Entity Relationships

```
┌──────────────┐
│   Customer   │ (External - stub only)
│--------------│
│ id: Long     │
│ name: String │
│ email: String│
└──────┬───────┘
       │ 1:N
       │
       ├─────────────────┐
       │                 │
       ▼ 1:N             ▼ 1:N
┌──────────────┐  ┌─────────────┐
│  CartItem    │  │    Order    │
│--------------│  │-------------│
│ id: Long     │  │ id: Long    │
│ customerId   │  │ customerId  │
│ productId    │  │ customerName│
│ quantity     │  │ orderDate   │
│ price        │  │ status      │
│ productName  │  │ totalAmount │
└──────────────┘  └──────┬──────┘
                         │ 1:N
                         ▼
                  ┌──────────────┐
                  │  OrderItem   │
                  │--------------│
                  │ id: Long     │
                  │ orderId      │
                  │ productId    │
                  │ quantity     │
                  │ price        │
                  │ productName  │
                  └──────────────┘

┌──────────────┐
│   Product    │ (External - stub only)
│--------------│
│ id: Long     │
│ name: String │
│ price: Double│
│ description  │
└──────────────┘
```

### Key Design Decisions

**1. Separate CartItem and OrderItem**
- **Rationale**: Shopping cart is mutable (add/remove/update), order history is immutable
- **Benefit**: Clear separation of concerns, simpler queries, better performance
- **Trade-off**: More entities, but clearer semantics

**2. Denormalized Product Data**
- **Rationale**: Store `productName` and `price` snapshot in CartItem/OrderItem
- **Benefit**: Historical accuracy (price changes don't affect past orders)
- **Trade-off**: Data duplication, but essential for auditing

**3. Customer ID in CartItem**
- **Rationale**: Enable customer-scoped queries, support data isolation
- **Benefit**: Security, clear ownership, enables sharding by customer
- **Trade-off**: Slight redundancy, but critical for multi-tenancy

---

## High-Level Architecture

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                        API Gateway                          │
│              (Authentication, Rate Limiting)                │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   Spring Boot Application                   │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │   Cart       │  │    Order     │  │   Customer   │    │
│  │  Controller  │  │  Controller  │  │   Stub Repo  │    │
│  └──────┬───────┘  └──────┬───────┘  └──────────────┘    │
│         │                  │                               │
│  ┌──────▼───────┐  ┌──────▼───────┐                      │
│  │   Cart       │  │    Order     │                       │
│  │   Service    │  │   Service    │                       │
│  └──────┬───────┘  └──────┬───────┘                      │
│         │                  │                               │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────────────┐   │
│  │  CartItem    │  │  Order       │  │  OrderItem   │   │
│  │  Repository  │  │  Repository  │  │  Repository  │   │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
│         │                  │                  │            │
└─────────┼──────────────────┼──────────────────┼────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                      │
│                                                             │
│     cart_items          orders          order_items        │
└─────────────────────────────────────────────────────────────┘
```

### Layered Architecture

```
┌────────────────────────────────────────────────┐
│           Presentation Layer                   │
│  (Controllers, DTOs, Exception Handlers)       │
│  - CartItemController                          │
│  - OrderController                             │
│  - GlobalExceptionHandler                      │
└────────────────┬───────────────────────────────┘
                 │
┌────────────────▼───────────────────────────────┐
│           Business Logic Layer                 │
│  (Services, Domain Logic, Validation)          │
│  - CartItemService                             │
│  - OrderService                                │
│  - State transition validation                 │
└────────────────┬───────────────────────────────┘
                 │
┌────────────────▼───────────────────────────────┐
│           Data Access Layer                    │
│  (Repositories, JPA Entities)                  │
│  - CartItemRepository                          │
│  - OrderRepository                             │
│  - OrderItemRepository                         │
└────────────────┬───────────────────────────────┘
                 │
┌────────────────▼───────────────────────────────┐
│           Database Layer                       │
│  (PostgreSQL, H2 for dev/test)                 │
└────────────────────────────────────────────────┘
```

---

## API Design

### Resource Hierarchy (RESTful)

```
/api/customers/{customerId}/
├── cart/
│   └── items/                 # Shopping cart items
│       ├── GET               # List all items in cart
│       ├── POST              # Add item to cart
│       ├── PUT /{itemId}     # Update item quantity
│       └── DELETE /{itemId}  # Remove item from cart
│
└── orders/                    # Customer orders
    ├── GET                    # List orders (paginated, filtered)
    ├── POST                   # Create order from cart (checkout)
    └── /{orderId}/
        ├── GET                # Get order details
        ├── PATCH /status      # Update order status
        └── POST /cancel       # Cancel order
```

### API Endpoints

#### Cart Management

##### 1. Get All Cart Items
```http
GET /api/customers/{customerId}/cart/items
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "customerId": 123,
    "productId": 1,
    "productName": "Laptop",
    "quantity": 2,
    "price": 999.99,
    "subtotal": 1999.98
  },
  {
    "id": 2,
    "customerId": 123,
    "productId": 2,
    "productName": "Mouse",
    "quantity": 1,
    "price": 29.99,
    "subtotal": 29.99
  }
]
```

##### 2. Add Item to Cart
```http
POST /api/customers/{customerId}/cart/items
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "customerId": 123,
  "productId": 1,
  "productName": "Laptop",
  "quantity": 2,
  "price": 999.99,
  "subtotal": 1999.98
}
```

**Error 404 Not Found:**
```json
{
  "message": "Product with id 999 not found",
  "status": 404,
  "timestamp": "2026-01-31T10:30:00",
  "errors": []
}
```

##### 3. Update Cart Item
```http
PUT /api/customers/{customerId}/cart/items/{itemId}
Content-Type: application/json

{
  "quantity": 3
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "customerId": 123,
  "productId": 1,
  "productName": "Laptop",
  "quantity": 3,
  "price": 999.99,
  "subtotal": 2999.97
}
```

##### 4. Delete Cart Item
```http
DELETE /api/customers/{customerId}/cart/items/{itemId}
```

**Response 204 No Content**

#### Order Management

##### 5. Create Order (Checkout)
```http
POST /api/customers/{customerId}/orders
Content-Type: application/json

{
  "customerName": "John Doe"
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "customerId": 123,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T10:30:00",
  "status": "CREATED",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop",
      "quantity": 2,
      "price": 999.99,
      "subtotal": 1999.98
    },
    {
      "id": 2,
      "productId": 2,
      "productName": "Mouse",
      "quantity": 1,
      "price": 29.99,
      "subtotal": 29.99
    }
  ],
  "totalAmount": 2029.97
}
```

**Error 422 Unprocessable Entity:**
```json
{
  "message": "Cannot checkout with an empty cart",
  "status": 422,
  "timestamp": "2026-01-31T10:30:00",
  "errors": []
}
```

##### 6. Get Order by ID
```http
GET /api/customers/{customerId}/orders/{orderId}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "customerId": 123,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T10:30:00",
  "status": "SHIPPED",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop",
      "quantity": 2,
      "price": 999.99,
      "subtotal": 1999.98
    }
  ],
  "totalAmount": 1999.98
}
```

##### 7. List Orders (Paginated)
```http
GET /api/customers/{customerId}/orders?page=0&size=10&sort=orderDate,desc
```

**Response 200 OK:**
```json
{
  "content": [
    {
      "id": 5,
      "customerId": 123,
      "customerName": "John Doe",
      "orderDate": "2026-01-31T10:30:00",
      "status": "DELIVERED",
      "totalAmount": 1999.98
    },
    {
      "id": 3,
      "customerId": 123,
      "customerName": "John Doe",
      "orderDate": "2026-01-30T14:20:00",
      "status": "SHIPPED",
      "totalAmount": 599.99
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 15,
  "totalPages": 2,
  "last": false,
  "first": true
}
```

##### 8. Filter Orders by Status
```http
GET /api/customers/{customerId}/orders?status=SHIPPED&page=0&size=10
```

##### 9. Filter Orders by Date Range
```http
GET /api/customers/{customerId}/orders?startDate=2026-01-01&endDate=2026-01-31&page=0&size=10
```

##### 10. Update Order Status
```http
PATCH /api/orders/{orderId}/status
Content-Type: application/json

{
  "status": "SHIPPED"
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "customerId": 123,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T10:30:00",
  "status": "SHIPPED",
  "totalAmount": 1999.98
}
```

**Error 422 Unprocessable Entity:**
```json
{
  "message": "Invalid status transition: DELIVERED -> SHIPPED",
  "status": 422,
  "timestamp": "2026-01-31T10:30:00",
  "errors": []
}
```

##### 11. State Transition Rules
Valid transitions defined in business logic:
```
CREATED → CONFIRMED → SHIPPED → DELIVERED
    ↓         ↓          ↓
CANCELLED   CANCELLED  CANCELLED
```

##### 12. Cancel Order
```http
POST /api/orders/{orderId}/cancel
```

**Response 200 OK:**
```json
{
  "id": 1,
  "customerId": 123,
  "customerName": "John Doe",
  "orderDate": "2026-01-31T10:30:00",
  "status": "CANCELLED",
  "totalAmount": 1999.98
}
```

---

## Database Schema

### CartItem Table
```sql
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,          -- ✅ NEW: Customer isolation
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,   -- Denormalized for performance
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_customer_id (customer_id),  -- ✅ Fast customer lookup
    INDEX idx_product_id (product_id),
    
    CONSTRAINT fk_cart_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(id) ON DELETE CASCADE
);
```

### Order Table
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,           -- CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_customer_id (customer_id),   -- ✅ Fast customer lookup
    INDEX idx_status (status),              -- ✅ Fast status filtering
    INDEX idx_order_date (order_date),      -- ✅ Fast date range queries
    INDEX idx_customer_status (customer_id, status),  -- ✅ Composite for common queries
    
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id)
        REFERENCES customers(id) ON DELETE RESTRICT,
    CONSTRAINT chk_status CHECK (status IN ('CREATED', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'))
);
```

### OrderItem Table (NEW)
```sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,    -- Denormalized snapshot
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL,        -- Denormalized snapshot
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_order_id (order_id),         -- ✅ Fast order item lookup
    INDEX idx_product_id (product_id),
    
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id)
        REFERENCES orders(id) ON DELETE CASCADE
);
```

### Key Schema Features

1. **Customer Isolation**: `customer_id` indexed in both `cart_items` and `orders`
2. **Performance Indexes**: Strategic indexes on frequently queried columns
3. **Data Denormalization**: Product name/price stored for historical accuracy
4. **Referential Integrity**: Foreign keys with appropriate cascade rules
5. **Check Constraints**: Enforce business rules at database level

---

## Deep Dive: Core Challenges

### Challenge 1: Customer Data Isolation

#### Problem
```java
// ❌ INSECURE: Any customer can access any cart
GET /api/cart/items
→ Returns ALL cart items across ALL customers
```

#### Solution
```java
// ✅ SECURE: Customer-scoped resources
GET /api/customers/123/cart/items
→ Returns ONLY customer 123's cart items

// Repository method
List<CartItem> findByCustomerId(Long customerId);
```

#### Implementation
```java
@RestController
@RequestMapping("/api/customers/{customerId}")
public class CartItemController {
    
    @GetMapping("/cart/items")
    public List<CartItemResponse> getCartItems(
            @PathVariable Long customerId) {
        // ✅ Customer ID from URL path - explicit ownership
        return cartItemService.getCartItemsByCustomer(customerId);
    }
    
    @PostMapping("/cart/items")
    public ResponseEntity<CartItemResponse> addCartItem(
            @PathVariable Long customerId,
            @Valid @RequestBody CartItemRequest request) {
        // ✅ Service receives customer context
        CartItemResponse response = cartItemService.addCartItem(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

#### Security Benefits
- **URL-based authorization**: API gateway can enforce `customerId` matches authenticated user
- **Database-level isolation**: Queries always filter by `customer_id`
- **Audit trail**: All operations traceable to specific customer
- **Sharding-ready**: Can partition data by `customer_id` for horizontal scaling

---

### Challenge 2: Cart vs. Order Item Separation

#### Problem
```java
// ❌ AMBIGUOUS: orderId sometimes null, sometimes set
CartItem {
    id: 1,
    orderId: null,      // In cart
    productId: 1,
    quantity: 2
}

CartItem {
    id: 2,
    orderId: 5,         // In order?
    productId: 1,
    quantity: 2
}
```

**Issues:**
- Semantically confusing (is it a cart item or order item?)
- Complex queries (`WHERE orderId IS NULL` for cart)
- Mixed concerns (mutable cart + immutable order history)

#### Solution
```java
// ✅ CLEAR: Separate entities with distinct purposes

// Shopping Cart (mutable)
CartItem {
    id: 1,
    customerId: 123,    // Ownership
    productId: 1,
    quantity: 2         // Can be updated
}

// Order History (immutable)
OrderItem {
    id: 1,
    orderId: 5,         // Part of order
    productId: 1,
    quantity: 2,        // Snapshot at purchase time
    price: 999.99       // Snapshot at purchase time
}
```

#### Implementation
```java
// CartItem - Active shopping cart
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;  // ✅ Clear ownership
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    private Integer quantity;
    private BigDecimal price;
    private String productName;
}

// OrderItem - Historical order record
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;     // ✅ Clear relationship
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    private Integer quantity;
    private BigDecimal price;       // ✅ Snapshot
    private String productName;     // ✅ Snapshot
}
```

---

### Challenge 3: Atomic Checkout Transaction

#### Problem
Checkout must be atomic (all-or-nothing):
1. Fetch cart items
2. Calculate total
3. Create order
4. Create order items from cart items
5. Clear cart

**If any step fails, entire operation should rollback.**

#### Solution: Transactional Service Method

```java
@Service
@Transactional
public class OrderService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Transactional
    public OrderResponse checkout(Long customerId, CheckoutRequest request) {
        // 1. Fetch customer's cart items
        List<CartItem> cartItems = cartItemRepository.findByCustomerId(customerId);
        
        // 2. Validate cart not empty
        if (cartItems.isEmpty()) {
            throw new BusinessRuleException("Cannot checkout with an empty cart");
        }
        
        // 3. Calculate total
        BigDecimal totalAmount = cartItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 4. Create order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setCustomerName(request.getCustomerName());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(totalAmount);
        
        Order savedOrder = orderRepository.save(order);
        
        // 5. Convert cart items to order items
        List<OrderItem> orderItems = cartItems.stream()
            .map(cartItem -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(savedOrder.getId());
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setProductName(cartItem.getProductName());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                return orderItem;
            })
            .collect(Collectors.toList());
        
        orderItemRepository.saveAll(orderItems);
        
        // 6. Clear cart (delete cart items)
        cartItemRepository.deleteAll(cartItems);
        
        // 7. Return order with items
        return convertToOrderResponse(savedOrder, orderItems);
    }
}
```

#### Transaction Guarantees
- **Atomicity**: All operations succeed or all rollback
- **Consistency**: Cart cleared only if order created
- **Isolation**: Other requests see consistent state
- **Durability**: Once committed, changes are permanent

---

### Challenge 4: Order State Transitions

#### Problem
Prevent invalid state transitions:
```
❌ DELIVERED → SHIPPED (can't un-deliver)
❌ CANCELLED → CONFIRMED (can't un-cancel)
❌ DELIVERED → CANCELLED (can't cancel delivered order)
```

#### Solution: State Machine Pattern

```java
@Service
public class OrderService {
    
    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
        OrderStatus.CREATED, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
        OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
        OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
        OrderStatus.DELIVERED, Set.of(),  // Final state
        OrderStatus.CANCELLED, Set.of()   // Final state
    );
    
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();
        
        // Validate transition
        if (!VALID_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            throw new BusinessRuleException(
                String.format("Invalid status transition: %s -> %s", 
                    currentStatus, newStatus)
            );
        }
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        return convertToOrderResponse(updatedOrder);
    }
}
```

#### State Diagram
```
    ┌─────────┐
    │ CREATED │
    └────┬────┘
         │
         ├──────────────┐
         ▼              ▼
    ┌──────────┐   ┌───────────┐
    │CONFIRMED │   │ CANCELLED │ (final)
    └────┬─────┘   └───────────┘
         │              ▲
         ├──────────────┤
         ▼              │
    ┌─────────┐        │
    │ SHIPPED │────────┤
    └────┬────┘        │
         │              │
         ├──────────────┘
         ▼
    ┌───────────┐
    │ DELIVERED │ (final)
    └───────────┘
```

---

### Challenge 5: Pagination & Filtering Performance

#### Problem
Without proper indexing:
```sql
-- ❌ SLOW: Full table scan on orders
SELECT * FROM orders WHERE customer_id = 123 AND status = 'SHIPPED'
ORDER BY order_date DESC LIMIT 10 OFFSET 0;
```

#### Solution: Strategic Indexing + JPA Specifications

**Database Indexes:**
```sql
-- Composite index for common query pattern
CREATE INDEX idx_customer_status ON orders(customer_id, status);

-- Index for date range queries
CREATE INDEX idx_order_date ON orders(order_date);
```

**JPA Repository:**
```java
public interface OrderRepository extends 
        JpaRepository<Order, Long>, 
        JpaSpecificationExecutor<Order> {
    
    // Simple queries use method naming
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    Page<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status, Pageable pageable);
    
    // Complex queries use Specifications (dynamic query building)
}
```

**Specification Pattern:**
```java
public class OrderSpecifications {
    
    public static Specification<Order> hasCustomerId(Long customerId) {
        return (root, query, cb) -> 
            cb.equal(root.get("customerId"), customerId);
    }
    
    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> 
            cb.equal(root.get("status"), status);
    }
    
    public static Specification<Order> orderDateBetween(
            LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> 
            cb.between(root.get("orderDate"), startDate, endDate);
    }
}

// Usage in service
public Page<OrderResponse> getOrders(
        Long customerId, 
        OrderStatus status, 
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable) {
    
    Specification<Order> spec = Specification.where(
        OrderSpecifications.hasCustomerId(customerId));
    
    if (status != null) {
        spec = spec.and(OrderSpecifications.hasStatus(status));
    }
    
    if (startDate != null && endDate != null) {
        spec = spec.and(OrderSpecifications.orderDateBetween(startDate, endDate));
    }
    
    Page<Order> orders = orderRepository.findAll(spec, pageable);
    return orders.map(this::convertToOrderResponse);
}
```

---

## Trade-offs & Alternatives

### Alternative 1: Keep Global Cart Endpoints

```
❌ /api/cart/items (current)
vs.
✅ /api/customers/{id}/cart/items (proposed)
```

**Pros of Global:**
- Simpler URL structure
- Fewer path parameters

**Cons of Global:**
- **Security risk**: No customer isolation
- **Non-RESTful**: Unclear resource ownership
- **Hard to scale**: Can't shard by customer
- **Authentication complexity**: Must pass customer ID in headers/body

**Decision**: Use customer-scoped resources for security and clarity

---

### Alternative 2: Single OrderItem Table (No CartItem)

```
❌ Orders table with items array field (JSON)
vs.
✅ Separate cart_items and order_items tables
```

**Pros of Single Table:**
- Less data duplication
- Simpler schema

**Cons of Single Table:**
- **Mixed semantics**: Cart (mutable) and orders (immutable) in one table
- **Complex queries**: `WHERE orderId IS NULL` for cart
- **Performance**: Harder to index and optimize
- **Atomic operations**: Harder to implement transactional checkout

**Decision**: Separate tables for clear semantics and performance

---

### Alternative 3: OrderItems as Nested JSON

```
❌ orders.items = JSON array
vs.
✅ order_items table with foreign key
```

**Pros of JSON:**
- No joins needed
- Simpler queries for single order

**Cons of JSON:**
- **Poor indexing**: Can't index product_id in JSON
- **Hard to aggregate**: Can't query "all orders containing product X"
- **Update complexity**: Must deserialize, modify, serialize
- **Database agnostic**: Loses relational database benefits

**Decision**: Use separate table for queryability and relational integrity

---

## Scalability & Performance

### Read Optimization

**1. Database Indexing**
```sql
-- Customer-centric queries (most common)
CREATE INDEX idx_customer_id ON cart_items(customer_id);
CREATE INDEX idx_customer_id ON orders(customer_id);
CREATE INDEX idx_customer_status ON orders(customer_id, status);

-- Product queries
CREATE INDEX idx_product_id ON cart_items(product_id);
CREATE INDEX idx_product_id ON order_items(product_id);

-- Date range queries
CREATE INDEX idx_order_date ON orders(order_date);
```

**2. Caching Strategy**
```java
// Cache frequently accessed data
@Cacheable(value = "orders", key = "#orderId")
public OrderResponse getOrderById(Long orderId) {
    // ...
}

// Cache customer's recent orders
@Cacheable(value = "customerOrders", key = "#customerId + '_' + #pageable.pageNumber")
public Page<OrderResponse> getCustomerOrders(Long customerId, Pageable pageable) {
    // ...
}

// Invalidate cache on updates
@CacheEvict(value = "orders", key = "#orderId")
public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
    // ...
}
```

**3. Pagination**
- Use cursor-based pagination for large datasets
- Limit max page size (e.g., 100 items)
- Index columns used in ORDER BY clauses

---

### Write Optimization

**1. Batch Operations**
```java
// Batch insert order items
orderItemRepository.saveAll(orderItems);

// Batch delete cart items
cartItemRepository.deleteAll(cartItems);
```

**2. Connection Pooling**
```properties
# application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

**3. Transaction Isolation**
```java
// Use appropriate isolation level
@Transactional(isolation = Isolation.READ_COMMITTED)
public OrderResponse checkout(Long customerId, CheckoutRequest request) {
    // Prevents dirty reads while allowing concurrent checkouts
}
```

---

### Horizontal Scaling

**1. Stateless Services**
- No session state in application servers
- Customer context from URL path (not server memory)
- Enables load balancing across multiple instances

**2. Database Sharding by Customer**
```
┌─────────────┐
│  Shard 1    │  customer_id % 4 = 0
│ (customers  │
│  0, 4, 8..) │
└─────────────┘

┌─────────────┐
│  Shard 2    │  customer_id % 4 = 1
│ (customers  │
│  1, 5, 9..) │
└─────────────┘

┌─────────────┐
│  Shard 3    │  customer_id % 4 = 2
│ (customers  │
│  2, 6, 10..)│
└─────────────┘

┌─────────────┐
│  Shard 4    │  customer_id % 4 = 3
│ (customers  │
│  3, 7, 11..)│
└─────────────┘
```

**3. Read Replicas**
- Route read queries to replicas
- Route writes to primary
- Use `@Transactional(readOnly = true)` for reads

---

## Security Considerations

### 1. Authorization Pattern

```java
@RestController
@RequestMapping("/api/customers/{customerId}")
public class CartItemController {
    
    @GetMapping("/cart/items")
    public List<CartItemResponse> getCartItems(
            @PathVariable Long customerId,
            @AuthenticationPrincipal User authenticatedUser) {
        
        // ✅ Verify authenticated user matches customerId
        if (!authenticatedUser.getId().equals(customerId)) {
            throw new ForbiddenException("Cannot access another customer's cart");
        }
        
        return cartItemService.getCartItemsByCustomer(customerId);
    }
}
```

### 2. API Gateway Integration

```
┌─────────────────┐
│   API Gateway   │
│  (AWS/Kong/     │
│   Ambassador)   │
└────────┬────────┘
         │
         │ 1. Authenticate user (JWT/OAuth)
         │ 2. Extract customer_id from token
         │ 3. Add customer_id to request header
         │ 4. Rate limit per customer
         │
         ▼
┌─────────────────┐
│  Spring Boot    │
│   Application   │
└─────────────────┘
```

### 3. SQL Injection Prevention

```java
// ✅ SAFE: Parameterized queries (JPA/Hibernate)
@Query("SELECT c FROM CartItem c WHERE c.customerId = :customerId")
List<CartItem> findByCustomerId(@Param("customerId") Long customerId);

// ❌ UNSAFE: String concatenation (NEVER DO THIS)
String sql = "SELECT * FROM cart_items WHERE customer_id = " + customerId;
```

### 4. Input Validation

```java
// ✅ Bean Validation
public class CartItemRequest {
    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Long productId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    private Integer quantity;
}
```

---

## Migration Path

### Phase 1: Database Migration (4-6 hours)

**Step 1: Add customer_id to cart_items**
```sql
ALTER TABLE cart_items 
ADD COLUMN customer_id BIGINT NOT NULL DEFAULT 1;

CREATE INDEX idx_customer_id ON cart_items(customer_id);
```

**Step 2: Create order_items table**
```sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
```

**Step 3: Migrate existing data**
```sql
-- Infer customer_id from order relationships (if exists)
UPDATE cart_items c
INNER JOIN orders o ON c.order_id = o.id
SET c.customer_id = o.customer_id
WHERE c.order_id IS NOT NULL;

-- For orphaned cart items, assign to default customer
UPDATE cart_items
SET customer_id = 1
WHERE customer_id IS NULL;
```

**Step 4: Remove order_id from cart_items**
```sql
ALTER TABLE cart_items DROP COLUMN order_id;
```

---

### Phase 2: Repository Updates (2-3 hours)

```java
// CartItemRepository - Add customer-scoped queries
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomerId(Long customerId);
    Optional<CartItem> findByIdAndCustomerId(Long id, Long customerId);
    void deleteByIdAndCustomerId(Long id, Long customerId);
}

// OrderItemRepository - NEW
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}
```

---

### Phase 3: Service Layer Refactoring (5-7 hours)

```java
@Service
public class CartItemService {
    
    // ✅ NEW: Customer context in all methods
    public List<CartItemResponse> getCartItemsByCustomer(Long customerId) {
        List<CartItem> items = cartItemRepository.findByCustomerId(customerId);
        return items.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public CartItemResponse addCartItem(Long customerId, CartItemRequest request) {
        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        CartItem cartItem = new CartItem();
        cartItem.setCustomerId(customerId);  // ✅ Set customer context
        cartItem.setProductId(request.getProductId());
        cartItem.setProductName(product.getName());
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(product.getPrice());
        
        CartItem saved = cartItemRepository.save(cartItem);
        return convertToResponse(saved);
    }
}
```

---

### Phase 4: Controller Updates (3-4 hours)

```java
@RestController
@RequestMapping("/api/customers/{customerId}")  // ✅ NEW: Customer-scoped
public class CartItemController {
    
    @GetMapping("/cart/items")
    public ResponseEntity<List<CartItemResponse>> getCartItems(
            @PathVariable Long customerId) {
        List<CartItemResponse> items = cartItemService.getCartItemsByCustomer(customerId);
        return ResponseEntity.ok(items);
    }
    
    @PostMapping("/cart/items")
    public ResponseEntity<CartItemResponse> addCartItem(
            @PathVariable Long customerId,
            @Valid @RequestBody CartItemRequest request) {
        CartItemResponse response = cartItemService.addCartItem(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

---

### Phase 5: Documentation & Testing (5-8 hours)

1. Update OpenAPI spec with new paths
2. Update all 13 task documentation files
3. Update solution hints
4. Create new test fixtures with customer_id
5. Update all test classes
6. Integration testing
7. Security testing

---

## Summary

### Key Design Principles

1. ✅ **Customer-Scoped Resources**: `/api/customers/{id}/cart/items` for security and clarity
2. ✅ **Separate Cart and Order Items**: Clear semantics, better performance
3. ✅ **Transactional Checkout**: Atomic cart-to-order conversion
4. ✅ **State Machine Pattern**: Enforced order state transitions
5. ✅ **Strategic Indexing**: Optimized for read-heavy workload
6. ✅ **RESTful Design**: Proper resource hierarchy and HTTP verbs

### Critical Improvements

| Aspect | Before | After |
|--------|--------|-------|
| **Security** | No customer isolation | Full customer isolation |
| **API Design** | Non-RESTful global endpoints | RESTful customer-scoped endpoints |
| **Data Model** | Ambiguous orderId | Clear CartItem/OrderItem separation |
| **Checkout** | Manual order creation | Atomic transactional checkout |
| **Performance** | No indexes on customer_id | Strategic indexing for common queries |

### Estimated Effort

- **Total**: 24-36 developer hours (1-2 weeks)
- **Risk Level**: Low (well-defined, incremental migration)
- **Impact**: High (all 13 tasks improved, better security, clearer design)

---

**Recommendation**: ✅ **IMPLEMENT FULL SOLUTION**

This design provides a production-ready, scalable, and secure foundation for e-commerce order management. The investment is justified by improved security, maintainability, and alignment with industry best practices.
