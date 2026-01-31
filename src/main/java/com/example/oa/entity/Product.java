package com.example.oa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product entity (STUB for OA practice).
 * 
 * This is a simplified stub entity. In a real application, this would have
 * more fields like description, category, stock, etc.
 * 
 * For the OA, assume products exist and can be referenced by ID.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column
    private String description;
}
