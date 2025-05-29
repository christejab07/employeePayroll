package com.erp.employeepayroll.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a message sent to an employee, typically related to payroll approval.
 */
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Builder // Provides a builder pattern for object creation
@Entity
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key, auto-generated

    @ManyToOne(fetch = FetchType.LAZY) // Many-to-one relationship with Employee
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Employee employee; // The employee to whom the message is sent

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message; // The content of the message

    @Column(nullable = false)
    private Integer month; // Month relevant to the message (e.g., payroll month)

    @Column(nullable = false)
    private Integer year; // Year relevant to the message (e.g., payroll year)

    @Column(nullable = false)
    private LocalDate sentDate; // Date when the message was generated/sent
}
