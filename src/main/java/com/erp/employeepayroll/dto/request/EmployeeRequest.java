package com.erp.employeepayroll.dto.request;

import com.erp.employeepayroll.model.Employee;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for creating or updating an Employee.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "Employee code cannot be blank")
    @Size(max = 50, message = "Employee code cannot exceed 50 characters")
    private String code;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    // Password is required for creation, but can be null for updates if not changing
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    private String password;

    @Size(max = 20, message = "Mobile number cannot exceed 20 characters")
    private String mobile;

    @PastOrPresent(message = "Date of birth cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @NotNull(message = "Status cannot be null")
    private Employee.EmployeeStatus status;

    @NotEmpty(message = "At least one role must be assigned")
    private Set<String> roles; // e.g., ["ROLE_EMPLOYEE", "ROLE_MANAGER"]
}
