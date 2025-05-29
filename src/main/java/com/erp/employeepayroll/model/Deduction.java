package com.erp.employeepayroll.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a type of deduction that can be applied to an employee's salary.
 * Stores the name of the deduction and its percentage.
 */
@Entity
@Data
public class Deduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key, auto-generated

    @Column(nullable = false, length = 50, unique = true)
    private String code; // Unique code for the deduction

    @Column(nullable = false, length = 100, unique = true)
    private String deductionName; // Name of the deduction (e.g., "Employee Tax", "Pension")

    @Column(nullable = false)
    private Double percentage; // Percentage of the base salary to be deducted (e.g., 0.05 for 5%)
}
