package com.erp.employeepayroll.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents the employment details of an employee.
 * This entity stores information about an employee's current or past employment.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key, auto-generated

    @Column(nullable = false, length = 50, unique = true)
    private String code; // Unique employment record code

    @OneToOne(fetch = FetchType.LAZY) // One-to-one relationship with Employee
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee; // The employee associated with this employment record

    @Column(nullable = false, length = 100)
    private String department; // Department where the employee works

    @Column(nullable = false, length = 100)
    private String position; // Employee's position/job title

    @Column(nullable = false)
    private Double baseSalary; // Base salary of the employee

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status; // Employment status: ACTIVE or INACTIVE

    @Column(nullable = false)
    private LocalDate joiningDate; // Date when the employee joined this employment

    // Enum for Employment Status
    public enum EmploymentStatus {
        ACTIVE, INACTIVE
    }

}