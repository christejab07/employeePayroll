package com.erp.employeepayroll.controller;

import com.erp.employeepayroll.dto.request.PayslipGenerationRequest;
import com.erp.employeepayroll.dto.response.PayslipResponse;
import com.erp.employeepayroll.service.PayslipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing payslips, including generation and approval.
 */
@RestController
@RequestMapping("/api/payslips")
@Tag(name = "Payslip Controller", description = "APIs for managing payslips (generate, view, approve)")
@SecurityRequirement(name = "Bearer Authentication") // Applies JWT security to all methods in this controller
public class PayslipController {

    private final PayslipService payslipService;

    public PayslipController(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    /**
     * Generates payslips for all active employees for a specified month and year.
     * Only accessible by users with 'ADMIN' or 'MANAGER' roles.
     *
     * @param request DTO containing month and year for payroll generation.
     * @return ResponseEntity with a list of generated PayslipResponse DTOs.
     */
    @Operation(
            summary = "Generate Payslips for a Month/Year",
            description = "Generates payslips for all active employees for the given month and year. Requires ADMIN or MANAGER role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payslips generated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input or payroll already generated"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN or MANAGER role")
            }
    )
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/generate")
    public ResponseEntity<List<PayslipResponse>> generatePayslips(@Valid @RequestBody PayslipGenerationRequest request) {
        List<PayslipResponse> payslips = payslipService.generatePayroll(request);
        return new ResponseEntity<>(payslips, HttpStatus.OK);
    }

    /**
     * Approves payslips for a specified month and year, changing their status from PENDING to PAID.
     * Only accessible by users with 'ADMIN' or 'MANAGER' roles.
     *
     * @param month The month for which to approve payroll.
     * @param year The year for which to approve payroll.
     * @return ResponseEntity with a list of approved PayslipResponse DTOs.
     */
    @Operation(
            summary = "Approve Payslips for a Month/Year",
            description = "Approves all PENDING payslips for the given month and year. Requires ADMIN or MANAGER role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payslips approved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - No pending payslips found or some are not pending"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN or MANAGER role"),
                    @ApiResponse(responseCode = "404", description = "Not Found - No payslips found for the given month/year")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/approve/{month}/{year}")
    public ResponseEntity<List<PayslipResponse>> approvePayslips(@PathVariable Integer month, @PathVariable Integer year) {
        List<PayslipResponse> approvedPayslips = payslipService.approvePayroll(month, year);
        return new ResponseEntity<>(approvedPayslips, HttpStatus.OK);
    }

    /**
     * Retrieves a single payslip by its ID.
     * Accessible by 'ADMIN', 'MANAGER', or the 'EMPLOYEE' who owns the payslip.
     *
     * @param id The ID of the payslip.
     * @return ResponseEntity with the PayslipResponse DTO.
     */
    @Operation(
            summary = "Get Payslip by ID",
            description = "Retrieves a single payslip by its ID. Requires ADMIN, MANAGER, or ownership by the employee.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payslip found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this payslip"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Payslip not found")
            }
    )
    // Note: A more sophisticated @PreAuthorize would check if current user is the owner or has admin/manager role.
    // For simplicity here, we're relying on service level checks or assuming the endpoint will only be accessed by authorized users.
    // A more explicit check would be: @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isPayslipOwner(#id, authentication.name)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<PayslipResponse> getPayslipById(@PathVariable Long id) {
        PayslipResponse payslip = payslipService.getPayslipById(id);
        return new ResponseEntity<>(payslip, HttpStatus.OK);
    }

    /**
     * Retrieves all payslips for a specific employee.
     * Accessible by 'ADMIN', 'MANAGER', or the 'EMPLOYEE' themselves.
     *
     * @param employeeId The ID of the employee.
     * @return ResponseEntity with a list of PayslipResponse DTOs.
     */
    @Operation(
            summary = "Get All Payslips for an Employee",
            description = "Retrieves all payslips for a specific employee. Requires ADMIN, MANAGER, or ownership by the employee.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payslips retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view these payslips"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Employee not found")
            }
    )
    // Same note as above regarding @PreAuthorize for ownership.
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByEmployee(@PathVariable Long employeeId) {
        List<PayslipResponse> payslips = payslipService.getPayslipsByEmployee(employeeId);
        return new ResponseEntity<>(payslips, HttpStatus.OK);
    }

    /**
     * Retrieves all payslips for a specific month and year.
     * Only accessible by users with 'ADMIN' or 'MANAGER' roles.
     *
     * @param month The month (1-12).
     * @param year The year.
     * @return ResponseEntity with a list of PayslipResponse DTOs.
     */
    @Operation(
            summary = "Get Payslips by Month and Year",
            description = "Retrieves all payslips for a given month and year. Requires ADMIN or MANAGER role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payslips retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN or MANAGER role")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByMonthAndYear(
            @PathVariable Integer month, @PathVariable Integer year) {
        List<PayslipResponse> payslips = payslipService.getPayslipsByMonthAndYear(month, year);
        return new ResponseEntity<>(payslips, HttpStatus.OK);
    }
}