package com.erp.employeepayroll.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) documentation configuration.
 * Defines API metadata, security schemes, and server information.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Rwanda ERP Payroll System API",
                version = "1.0",
                description = "Backend APIs for Employee and Payroll Management in the Government of Rwanda ERP system."
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server")
                // Add more server URLs for different environments (e.g., production)
        }
)
@SecurityScheme(
        name = "Bearer Authentication", // Name for the security scheme
        type = SecuritySchemeType.HTTP,  // Type of security scheme (HTTP for Bearer)
        bearerFormat = "JWT",            // Format of the bearer token
        scheme = "bearer",               // Scheme used for authentication
        description = "Provide JWT Bearer Token to access secured endpoints."
)
public class OpenApiConfig {
    // This class is primarily for annotations; no bean definitions are typically needed here.
    // The @OpenAPIDefinition and @SecurityScheme annotations are picked up by Springdoc.
}