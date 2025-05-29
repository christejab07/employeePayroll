package com.erp.employeepayroll.controller;

import com.erp.employeepayroll.dto.response.MessageResponse;
import com.erp.employeepayroll.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Messages.
 * Provides endpoints for employees and managers to view system-generated messages,
 * especially related to payroll notifications.
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "API for viewing system-generated messages and payroll notifications.")
@SecurityRequirement(name = "Bearer Authentication") // Applies JWT security to all endpoints in this controller
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(
            summary = "Get a message by ID",
            description = "Retrieves a specific message by its unique ID. Accessible by Admins, Managers, and the Employee who owns the message.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Message not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Access denied")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')") // More granular check might be needed in service for EMPLOYEE role
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable Long id) {
        MessageResponse message = messageService.getMessageById(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all messages for a specific employee",
            description = "Retrieves all messages sent to a particular employee. Accessible by Admins and Managers. An Employee can only retrieve their own messages.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Employee not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Access denied")
            }
    )
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')") // Assumes authentication.principal.id provides employee ID
    public ResponseEntity<List<MessageResponse>> getMessagesByEmployee(@PathVariable Long employeeId) {
        List<MessageResponse> messages = messageService.getMessagesByEmployee(employeeId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}