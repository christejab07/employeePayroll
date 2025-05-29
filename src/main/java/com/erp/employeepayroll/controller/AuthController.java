package com.erp.employeepayroll.controller;

import com.erp.employeepayroll.dto.request.AuthRequest;
import com.erp.employeepayroll.dto.request.EmployeeRequest;
import com.erp.employeepayroll.dto.response.EmployeeResponse;
import com.erp.employeepayroll.dto.response.JwtAuthResponse;
import com.erp.employeepayroll.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user authentication and registration.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "APIs for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user login and generates a JWT token upon successful authentication.
     *
     * @param authRequest DTO containing user's email and password.
     * @return ResponseEntity with JwtAuthResponse containing the access token.
     */
    @Operation(
            summary = "User Login",
            description = "Authenticates a user with email and password, returning a JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        JwtAuthResponse response = authService.login(authRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Registers a new employee user in the system.
     *
     * @param employeeRequest DTO containing new employee's details for registration.
     * @return ResponseEntity with EmployeeResponse of the registered employee.
     */
    @Operation(
            summary = "Register New Employee",
            description = "Registers a new employee account in the system. Default role is ROLE_EMPLOYEE, but can specify others.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Employee registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input or email/code already exists")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<EmployeeResponse> registerEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        EmployeeResponse response = authService.registerEmployee(employeeRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}