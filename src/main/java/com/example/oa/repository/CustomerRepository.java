package com.example.oa.repository;

import com.example.oa.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Customer repository interface (STUB for OA practice).
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
