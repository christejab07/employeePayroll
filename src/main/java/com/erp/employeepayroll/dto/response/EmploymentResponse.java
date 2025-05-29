package com.erp.employeepayroll.dto.response;

import com.erp.employeepayroll.model.Employment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for responding with Employment details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentResponse {
    private Long id;
    private String code;
    private Long employeeId; // Employee ID
    private String employeeCode; // Employee's unique code
    private String employeeFirstName; // Employee's first name
    private String employeeLastName; // Employee's last name
    private String department;
    private String position;
    private Double baseSalary;
    private Employment.EmploymentStatus status;
    private LocalDate joiningDate;
}
