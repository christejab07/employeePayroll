package com.erp.employeepayroll.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents an employee's payslip for a specific month and year.
 * Stores calculated salary components and deduction amounts.
 */
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Builder // Provides a builder pattern for object creation
@Entity
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key, auto-generated

    @ManyToOne(fetch = FetchType.LAZY) // Many-to-one relationship with Employee
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee; // The employee this payslip belongs to

    @Column(nullable = false)
    private Double baseSalaryAtGeneration; // Base salary used for this payslip calculation

    @Column(nullable = false)
    private Double houseAmount; // Calculated housing allowance amount

    @Column(nullable = false)
    private Double transportAmount; // Calculated transport allowance amount

    @Column(nullable = false)
    private Double employeeTaxedAmount; // Calculated employee tax amount

    @Column(nullable = false)
    private Double pensionAmount; // Calculated pension amount

    @Column(nullable = false)
    private Double medicalInsuranceAmount; // Calculated medical insurance amount

    @Column(nullable = false)
    private Double otherTaxedAmount; // Calculated 'others' deduction amount

    @Column(nullable = false)
    private Double grossSalary; // Total gross salary

    @Column(nullable = false)
    private Double netSalary; // Total net salary after all deductions

    @Column(nullable = false)
    private Integer month; // Month for which the payslip is generated (1-12)

    @Column(nullable = false)
    private Integer year; // Year for which the payslip is generated

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayslipStatus status; // Payslip status: PENDING or PAID

    @Column(nullable = false)
    private LocalDate generationDate; // Date when the payslip was generated

    private LocalDate approvalDate; // Date when the payslip was approved (if status is PAID)

    // Enum for Payslip Status
    public enum PayslipStatus {
        PENDING, PAID
    }
}