package com.erp.employeepayroll.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom AuthenticationEntryPoint for handling unauthorized requests in a JWT-based system.
 * Sends a 401 Unauthorized response if authentication fails.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * This is invoked when a user tries to access a secured REST resource without supplying any credentials
     * or supplying invalid ones.
     *
     * @param request The request that resulted in an AuthenticationException.
     * @param response The response.
     * @param authException The exception that was thrown.
     * @throws IOException If an input or output exception occurs.
     * @throws ServletException If a servlet-specific error occurs.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // This is invoked when user tries to access a protected resource without Authentication
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
