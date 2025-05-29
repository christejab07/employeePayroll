package com.erp.employeepayroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JWT authentication responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer"; // Default token type
    private String employeeCode;
    private String employeeEmail;
    private String roles; // Comma-separated roles for display

    public JwtAuthResponse(String accessToken, String employeeCode, String employeeEmail, String roles) {
        this.accessToken = accessToken;
        this.employeeCode = employeeCode;
        this.employeeEmail = employeeEmail;
        this.roles = roles;
    }
}