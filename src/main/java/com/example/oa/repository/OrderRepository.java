package com.example.oa.repository;

import com.example.oa.entity.Order;
import com.example.oa.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Order repository interface.
 * 
 * This interface is provided as part of the skeleton.
 * Candidates may need to add custom query methods for filtering operations.
 * 
 * Extends JpaSpecificationExecutor for advanced filtering (optional approach).
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // TODO: Task 7-9 - Add custom query methods for filtering
    // Examples:
    // Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    // Page<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
