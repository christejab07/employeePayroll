package com.erp.employeepayroll.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for requesting payroll generation for a specific month and year.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipGenerationRequest {

    @NotNull(message = "Month cannot be null")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year cannot be null")
    @Min(value = 2000, message = "Year must be 2000 or later") // Adjust as per business rule
    private Integer year;
}