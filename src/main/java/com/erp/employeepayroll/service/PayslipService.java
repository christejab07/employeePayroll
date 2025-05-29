package com.erp.employeepayroll.service;

import com.erp.employeepayroll.dto.request.PayslipGenerationRequest;
import com.erp.employeepayroll.dto.response.PayslipResponse;
import com.erp.employeepayroll.exception.ExcessiveDeductionsException;
import com.erp.employeepayroll.exception.ResourceNotFoundException;
import com.erp.employeepayroll.model.*;
import com.erp.employeepayroll.repository.*;

import io.swagger.v3.oas.models.info.Contact;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for payroll generation, viewing, and approval of payslips.
 */
@Service
@RequiredArgsConstructor
public class PayslipService {

    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionRepository deductionRepository;
    private final MessageService messageService; // Inject MessageService for post-approval messaging
    private final EmailService emailService;

    /**
     * Generates payslips for all active employees for a given month and year.
     * Prevents duplicate generation for the same month/year.
     *
     * @param request PayslipGenerationRequest containing month and year.
     * @return List of generated PayslipResponse DTOs.
     * @throws RuntimeException if payroll for the specified month/year already exists.
     */
    @Transactional
    public List<PayslipResponse> generatePayroll(PayslipGenerationRequest request) {
        Integer month = request.getMonth();
        Integer year = request.getYear();

        // Prevent duplicate payroll generation
        List<Payslip> existingPayslips = payslipRepository.findByMonthAndYear(month, year);
        if (!existingPayslips.isEmpty()) {
            // Check if any of them are NOT pending. If any are paid, prevent re-generation.
            // If all are pending, then it implies a previous generation attempt that wasn't approved.
            // For simplicity, we'll prevent re-generation if ANY exists, assuming "pending" status can be overwritten/retried
            // by deleting existing ones if needed, or by a specific re-generation endpoint.
            // For now, simple prevention.
            if (existingPayslips.stream().anyMatch(p -> p.getStatus() == Payslip.PayslipStatus.PAID)) {
                throw new RuntimeException("Payroll for " + month + "/" + year + " has already been approved. Cannot re-generate.");
            } else {
                // If there are existing PENDING payslips, we assume we can delete them and regenerate.
                // This might be a business decision. For this implementation, we will delete and regenerate.
                payslipRepository.deleteAll(existingPayslips);
            }
        }

        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(e -> e.getStatus() == Employee.EmployeeStatus.ACTIVE)
                .toList();

        List<Deduction> deductions = deductionRepository.findAll();
        Map<String, Double> deductionPercentages = deductions.stream()
                .collect(Collectors.toMap(Deduction::getDeductionName, Deduction::getPercentage));

        // Define deduction names from the project description for easy access
        Double employeeTax = deductionPercentages.getOrDefault("Employee Tax", 0.3);
        Double pension = deductionPercentages.getOrDefault("Pension", 0.06);
        Double medicalInsurance = deductionPercentages.getOrDefault("MedicalInsurance", 0.5);
        Double others = deductionPercentages.getOrDefault("Others", 0.5);
        Double housing = deductionPercentages.getOrDefault("Housing", 0.14);
        Double transport = deductionPercentages.getOrDefault("Transport", 0.14);

        List<Payslip> generatedPayslips = activeEmployees.stream()
                .map(employee -> {
                    Employment activeEmployment = employmentRepository.findByEmployeeAndStatus(employee, Employment.EmploymentStatus.ACTIVE)
                            .orElse(null); // Employee might not have an active employment

                    if (activeEmployment == null) {
                        System.out.println("Skipping employee " + employee.getCode() + ": No active employment found.");
                        return null; // Skip employees without active employment
                    }

                    Double baseSalary = activeEmployment.getBaseSalary();

                    // Gross Salary Calculation
                    Double housingAmount = baseSalary * housing;
                    Double transportAmount = baseSalary * transport;
                    Double grossSalary = baseSalary + housingAmount + transportAmount;

                    // Deductions Calculation
                    Double employeeTaxedAmount = baseSalary * employeeTax;
                    Double pensionAmount = baseSalary * pension;
                    Double medicalInsuranceAmount = baseSalary * medicalInsurance;
                    Double otherTaxedAmount = baseSalary * others;

                    // Net Salary Calculation
                    Double totalDeductions = employeeTaxedAmount + pensionAmount + medicalInsuranceAmount + otherTaxedAmount;

                    // Ensure deductions do not exceed gross salary (as a safeguard, though unlikely with current formula)
                    double netSalary;
                    if (totalDeductions > grossSalary) {
                        throw new ExcessiveDeductionsException(
                                String.format("Total deductions (%.2f) exceed gross salary (%.2f) for employee %s",
                                        totalDeductions,
                                        grossSalary,
                                        employee.getCode())
                        );
                    } else {
                        netSalary = grossSalary - totalDeductions;
                    }


                    Payslip payslip = Payslip.builder()
                            .employee(employee)
                            .baseSalaryAtGeneration(baseSalary)
                            .houseAmount(housingAmount)
                            .transportAmount(transportAmount)
                            .employeeTaxedAmount(employeeTaxedAmount)
                            .pensionAmount(pensionAmount)
                            .medicalInsuranceAmount(medicalInsuranceAmount)
                            .otherTaxedAmount(otherTaxedAmount)
                            .grossSalary(grossSalary)
                            .netSalary(netSalary)
                            .month(month)
                            .year(year)
                            .status(Payslip.PayslipStatus.PENDING) // Initially pending
                            .generationDate(LocalDate.now())
                            .build();

                    return payslip;
                })
                .filter(Objects::nonNull) // Filter out nulls from skipped employees
                .collect(Collectors.toList());

        List<Payslip> savedPayslips = payslipRepository.saveAll(generatedPayslips);
        return savedPayslips.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Approves payslips for a given month and year, changing their status from PENDING to PAID.
     * Triggers message generation for each approved payslip.
     * @param month The month for which to approve payroll.
     * @param year The year for which to approve payroll.
     * @return List of approved PayslipResponse DTOs.
     * @throws ResourceNotFoundException if no pending payslips are found for the given month/year.
     * @throws RuntimeException if some payslips for the period are already paid.
     */
    @Transactional
    public List<PayslipResponse> approvePayroll(Integer month, Integer year) {
        List<Payslip> payslipsToApprove = payslipRepository.findByMonthAndYear(month, year);

        if (payslipsToApprove.isEmpty()) {
            throw new ResourceNotFoundException("Payslips"+ "month/year"+ month + "/" + year + " (no pending payslips found)");
        }

        // Ensure all are pending before approving
        boolean allPending = payslipsToApprove.stream()
                .allMatch(p -> p.getStatus() == Payslip.PayslipStatus.PENDING);

        if (!allPending) {
            throw new RuntimeException("Cannot approve payroll: Some payslips for " + month + "/" + year + " are not in PENDING status.");
        }

        for (Payslip payslip : payslipsToApprove) {
            payslip.setStatus(Payslip.PayslipStatus.PAID);
            payslip.setApprovalDate(LocalDate.now());
            payslipRepository.save(payslip); // Save each approved payslip

            // Trigger message generation
            messageService.createPayslipApprovalMessage(
                    payslip.getEmployee(),
                    payslip.getNetSalary(),
                    payslip.getMonth(),
                    payslip.getYear()
            );
            // Send email notification
            String subject = "Salary Payment Notification - " + month + "/" + year;
            Employee employee = new Employee();
            String emailContent = String.format("""
                    <html>
                    <body>
                    <h2>Salary Payment Notification</h2>
                    <p>Dear %s %s,</p>
                    <p>Your salary for %d/%d has been processed and approved with the following details:</p>
                    <ul>
                        <li>Gross Salary: %.2f RWF</li>
                        <li>Net Salary: %.2f RWF</li>
                        <li>Approval Date: %s</li>
                    </ul>
                    <p>Your salary has been credited to your account.</p>
                    <p>Best regards,<br/>HR Department</p>
                    </body>
                    </html>
                    """,
                    employee.getFirstName(),
                    employee.getLastName(),
                    month,
                    year,
                    payslip.getGrossSalary(),
                    payslip.getNetSalary(),
                    payslip.getApprovalDate()
            );
            emailService.sendPayslipEmail(employee.getEmail(), subject, emailContent);


        }

        return payslipsToApprove.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific payslip by its ID.
     * @param payslipId The ID of the payslip.
     * @return PayslipResponse DTO.
     * @throws ResourceNotFoundException if payslip is not found.
     */
    public PayslipResponse getPayslipById(Long payslipId) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip id"+ payslipId));

        // Get authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmployeeCode = authentication.getName();

        // Check if the authenticated user has ROLE_EMPLOYEE and is trying to access another employee's payslip
        boolean isEmployeeRole = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isEmployeeRole && !payslip.getEmployee().getCode().equals(authenticatedEmployeeCode)) {
            throw new AccessDeniedException("You are not authorized to access other employees' payslips.");
        }

        return mapToResponse(payslip);
    }

    /**
     * Retrieves all payslips for a specific employee.
     * @param employeeId The ID of the employee.
     * @return List of PayslipResponse DTOs.
     * @throws ResourceNotFoundException if employee is not found.
     */
    public List<PayslipResponse> getPayslipsByEmployee(Long employeeId) {
        // Get authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmployeeCode = authentication.getName();

        Employee requestedEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee id"+ employeeId));

        // Check if the authenticated user has ROLE_EMPLOYEE and is trying to access another employee's payslips
        boolean isEmployeeRole = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isEmployeeRole && !requestedEmployee.getCode().equals(authenticatedEmployeeCode)) {
            throw new AccessDeniedException("You are not authorized to access other employees' payslips.");
        }

        List<Payslip> payslips = payslipRepository.findByEmployee(requestedEmployee);
        return payslips.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all payslips for a given month and year.
     * @param month The month (1-12).
     * @param year The year.
     * @return List of PayslipResponse DTOs.
     */
    public List<PayslipResponse> getPayslipsByMonthAndYear(Integer month, Integer year) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYear(month, year);
        // No employee-specific access check here as it's for general payroll overview
        return payslips.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method to map Payslip entity to PayslipResponse DTO
    private PayslipResponse mapToResponse(Payslip payslip) {
        return PayslipResponse.builder()
                .id(payslip.getId())
                .employeeId(payslip.getEmployee().getId())
                .employeeCode(payslip.getEmployee().getCode())
                .employeeFirstName(payslip.getEmployee().getFirstName())
                .employeeLastName(payslip.getEmployee().getLastName())
                .baseSalaryAtGeneration(payslip.getBaseSalaryAtGeneration())
                .houseAmount(payslip.getHouseAmount())
                .transportAmount(payslip.getTransportAmount())
                .employeeTaxedAmount(payslip.getEmployeeTaxedAmount())
                .pensionAmount(payslip.getPensionAmount())
                .medicalInsuranceAmount(payslip.getMedicalInsuranceAmount())
                .otherTaxedAmount(payslip.getOtherTaxedAmount())
                .grossSalary(payslip.getGrossSalary())
                .netSalary(payslip.getNetSalary())
                .month(payslip.getMonth())
                .year(payslip.getYear())
                .status(payslip.getStatus())
                .generationDate(payslip.getGenerationDate())
                .approvalDate(payslip.getApprovalDate())
                .build();
    }
}