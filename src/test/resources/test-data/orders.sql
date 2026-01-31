-- Test data for orders with various statuses
INSERT INTO orders (id, customer_id, customer_name, order_date, status, total_amount) VALUES
(1, 1, 'John Doe', '2026-01-15T10:30:00', 'CREATED', 2029.97),
(2, 2, 'Jane Smith', '2026-01-20T14:00:00', 'CONFIRMED', 599.99),
(3, 1, 'John Doe', '2026-01-25T09:15:00', 'SHIPPED', 1299.99),
(4, 3, 'Alice Johnson', '2026-01-28T16:45:00', 'DELIVERED', 379.98),
(5, 2, 'Jane Smith', '2026-01-30T11:20:00', 'CANCELLED', 149.99),
(6, 1, 'John Doe', '2026-01-18T12:00:00', 'PENDING', 899.99),
(7, 3, 'Alice Johnson', '2026-01-29T15:30:00', 'COMPLETED', 249.99);
