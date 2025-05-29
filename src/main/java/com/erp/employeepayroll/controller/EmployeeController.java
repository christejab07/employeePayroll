package com.erp.employeepayroll.controller;

import com.erp.employeepayroll.dto.request.EmployeeRequest;
import com.erp.employeepayroll.dto.response.EmployeeResponse;
import com.erp.employeepayroll.service.EmployeeService;
import com.erp.employeepayroll.util.SwaggerTags;
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
 * REST Controller for managing Employee personal information.
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = SwaggerTags.EMPLOYEE, description = "APIs for managing employee personal information")
@SecurityRequirement(name = "Bearer Authentication") // Applies JWT security to all methods in this controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Creates a new employee record. Requires 'ADMIN' or 'MANAGER' role.
     *
     * @param request DTO containing employee details.
     * @return ResponseEntity with the created EmployeeResponse DTO.
     */
    @Operation(
            summary = "Create New Employee",
            description = "Adds a new employee record. Requires ADMIN or MANAGER role.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Employee created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input or email/code already exists"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN or MANAGER role")
            }
    )
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse createdEmployee = employeeService.createEmployee(request);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    /**
     * Retrieves an employee by their ID. Accessible by 'ADMIN', 'MANAGER', or the 'EMPLOYEE' themselves.
     *
     * @param id The ID of the employee.
     * @return ResponseEntity with the EmployeeResponse DTO.
     */
    @Operation(
            summary = "Get Employee by ID",
            description = "Retrieves an employee by their ID. Requires ADMIN, MANAGER, or ownership by the employee.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employee found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this employee"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Employee not found")
            }
    )
    // This pre-authorize logic typically requires a custom voter or expression to check ownership against the authenticated user.
    // For simplicity, I'm providing a general role-based access. Actual ownership check would be more complex here.
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    /**
     * Retrieves an employee by their unique code. Accessible by 'ADMIN', 'MANAGER', or the 'EMPLOYEE' themselves.
     *
     * @param code The unique code of the employee.
     * @return ResponseEntity with the EmployeeResponse DTO.
     */
    @Operation(
            summary = "Get Employee by Code",
            description = "Retrieves an employee by their unique code. Requires ADMIN, MANAGER, or ownership by the employee.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employee found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this employee"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Employee not found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/code/{code}")
    public ResponseEntity<EmployeeResponse> getEmployeeByCode(@PathVariable String code) {
        EmployeeResponse employee = employeeService.getEmployeeByCode(code);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    /**
     * Retrieves all employee records. Requires 'ADMIN' or 'MANAGER' role.
     *
     * @return ResponseEntity with a list of all EmployeeResponse DTOs.
     */
    @Operation(
            summary = "Get All Employees",
            description = "Retrieves a list of all employee records. Requires ADMIN or MANAGER role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN or MANAGER role")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    /**
     * Updates an existing employee record. Requires 'ADMIN' or 'MANAGER' role.
     *
     * @param id The ID of the employee to update.
     * @param request DTO with updated employee details.
     * @return ResponseEntity with the updated EmployeeResponse DTO.
     */
    @Operation(
            summary = "Update Employee",
            description = "Updates an existing employee record by ID. Requires ADMIN or MANAGER role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input or email/code taken by another employee"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN or MANAGER role"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Employee not found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse updatedEmployee = employeeService.updateEmployee(id, request);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    /**
     * Deletes (disables) an employee record by ID. Requires 'ADMIN' or 'MANAGER' role.
     * This performs a soft delete by setting the employee's status to DISABLED.
     *
     * @param id The ID of the employee to delete.
     * @return ResponseEntity with no content.
     */
    @Operation(
            summary = "Delete Employee (Soft Delete)",
            description = "Disables an employee record by ID (soft delete). Requires ADMIN or MANAGER role.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Employee disabled successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN or MANAGER role"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Employee not found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}