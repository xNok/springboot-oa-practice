package com.example.oa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Customer entity (STUB for OA practice).
 * 
 * This is a simplified stub entity. In a real application, this would have
 * more fields like address, phone, email, etc.
 * 
 * For the OA, assume customers exist and can be referenced by ID.
 */
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String email;
}
