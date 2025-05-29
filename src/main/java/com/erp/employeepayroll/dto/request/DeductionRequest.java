package com.erp.employeepayroll.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a Deduction record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductionRequest {

    @NotBlank(message = "Deduction code cannot be blank")
    @Size(max = 50, message = "Deduction code cannot exceed 50 characters")
    private String code;

    @NotBlank(message = "Deduction name cannot be blank")
    @Size(max = 100, message = "Deduction name cannot exceed 100 characters")
    private String deductionName;

    @NotNull(message = "Percentage cannot be null")
    @DecimalMin(value = "0.0", message = "Percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100")
    private Double percentage; // Store as 5.0 for 5%
}
