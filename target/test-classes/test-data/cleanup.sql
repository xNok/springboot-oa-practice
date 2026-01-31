-- Cleanup script to reset database after tests
DELETE FROM cart_items;
DELETE FROM orders;
DELETE FROM products;
DELETE FROM customers;
