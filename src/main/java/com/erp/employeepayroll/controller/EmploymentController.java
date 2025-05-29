package com.erp.employeepayroll.controller;

import com.erp.employeepayroll.dto.request.EmploymentRequest;
import com.erp.employeepayroll.dto.response.EmploymentResponse;
import com.erp.employeepayroll.service.EmploymentService;
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
 * REST Controller for managing Employment records.
 * Provides endpoints for creating, retrieving, updating, and deleting employment details.
 */
@RestController
@RequestMapping("/api/employments")
@Tag(name = SwaggerTags.EMPLOYMENT, description = "API for managing employee employment records.")
@SecurityRequirement(name = "Bearer Authentication") // Applies JWT security to all endpoints in this controller
public class EmploymentController {

    private final EmploymentService employmentService;

    public EmploymentController(EmploymentService employmentService) {
        this.employmentService = employmentService;
    }

    @Operation(
            summary = "Create a new employment record",
            description = "Creates a new employment entry for an employee. Only accessible by ADMIN and MANAGER roles.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Employment record created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or employment already exists"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Access denied"),
                    @ApiResponse(responseCode = "404", description = "Employee not found")
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponse> createEmployment(@Valid @RequestBody EmploymentRequest request) {
        EmploymentResponse response = employmentService.createEmployment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get employment record by ID",
            description = "Retrieves a specific employment record by its unique ID. Accessible by ADMIN, MANAGER, and the associated EMPLOYEE.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employment record retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Employment record not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Access denied")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')") // Employee can view their own employment, needs service-level check
    public ResponseEntity<EmploymentResponse> getEmploymentById(@PathVariable Long id) {
        EmploymentResponse response = employmentService.getEmploymentById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all employment records",
            description = "Retrieves a list of all employment records. Only accessible by ADMIN and MANAGER roles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of employment records retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Access denied")
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentResponse>> getAllEmployments() {
        List<EmploymentResponse> responses = employmentService.getAllEmployments();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing employment record",
            description = "Updates an existing employment record identified by its ID. Only accessible by ADMIN and MANAGER roles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employment record updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or conflict (e.g., duplicate active employment)"),
                    @ApiResponse(responseCode = "404", description = "Employment record or associated employee not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Access denied")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponse> updateEmployment(@PathVariable Long id, @Valid @RequestBody EmploymentRequest request) {
        EmploymentResponse response = employmentService.updateEmployment(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete an employment record",
            description = "Deletes an employment record identified by its ID. Only accessible by ADMIN and MANAGER roles.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Employment record deleted successfully (No Content)"),
                    @ApiResponse(responseCode = "404", description = "Employment record not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Access denied")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteEmployment(@PathVariable Long id) {
        employmentService.deleteEmployment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}