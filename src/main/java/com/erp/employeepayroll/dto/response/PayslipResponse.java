package com.erp.employeepayroll.dto.response;

import com.erp.employeepayroll.model.Payslip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for responding with Payslip details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipResponse {
    private Long id;
    private Long employeeId; // Employee ID
    private String employeeCode; // Employee's unique code
    private String employeeFirstName;
    private String employeeLastName;
    private Double baseSalaryAtGeneration;
    private Double houseAmount;
    private Double transportAmount;
    private Double employeeTaxedAmount;
    private Double pensionAmount;
    private Double medicalInsuranceAmount;
    private Double otherTaxedAmount;
    private Double grossSalary;
    private Double netSalary;
    private Integer month;
    private Integer year;
    private Payslip.PayslipStatus status;
    private LocalDate generationDate;
    private LocalDate approvalDate;
}