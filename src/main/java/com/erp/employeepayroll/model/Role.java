package com.erp.employeepayroll.model;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a user role in the system.
 * Used for role-based access control (RBAC).
 */
@Entity
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Primary key, auto-generated

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, unique = true)
    private ERole name; // Role name (e.g., ROLE_MANAGER, ROLE_ADMIN, ROLE_EMPLOYEE)
}
