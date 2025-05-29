package com.erp.employeepayroll.dto.request;

import com.erp.employeepayroll.model.Employment;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO for creating or updating an Employment record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentRequest {

    @NotBlank(message = "Employment code cannot be blank")
    @Size(max = 50, message = "Employment code cannot exceed 50 characters")
    private String code;

    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId; // ID of the employee this employment belongs to

    @NotBlank(message = "Department cannot be blank")
    @Size(max = 100, message = "Department cannot exceed 100 characters")
    private String department;

    @NotBlank(message = "Position cannot be blank")
    @Size(max = 100, message = "Position cannot exceed 100 characters")
    private String position;

    @NotNull(message = "Base salary cannot be null")
    @PositiveOrZero(message = "Base salary must be a non-negative value")
    private Double baseSalary;

    @NotNull(message = "Status cannot be null")
    private Employment.EmploymentStatus status;

    @NotNull(message = "Joining date cannot be null")
    @PastOrPresent(message = "Joining date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joiningDate;
}