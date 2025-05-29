package com.erp.employeepayroll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an employee in the ERP system.
 * This entity stores personal and authentication details for an employee.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key, auto-generated

    @Column( nullable = false, length = 50)
    private String code; // Unique employee code

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName; // Employee's first name

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName; // Employee's last name

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email; // Employee's email, used as username for login

    @Column(nullable = false)
    private String password; // Hashed password for authentication

    @NotBlank(message = "Phone cannot be blank")
    @Pattern(regexp = "\\+2507[0-9]{8}", message = "Phone must be a valid Rwandan number starting with +2507 followed by 8 digits")
    private String mobile; // Employee's mobile number

    @Column(nullable = false)
    private LocalDate dateOfBirth; // Employee's date of birth

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status; // Employee status: ACTIVE or DISABLED

    @ManyToMany(fetch = FetchType.EAGER) // Roles are eagerly fetched with the employee
    @JoinTable(name = "roles",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>(); // Set of roles assigned to the employee

    // Enum for Employee Status
    public enum EmployeeStatus {
        ACTIVE, DISABLED
    }

}