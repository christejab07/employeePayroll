package com.erp.employeepayroll.service;

import com.erp.employeepayroll.dto.response.MessageResponse;
import com.erp.employeepayroll.exception.ResourceNotFoundException;
import com.erp.employeepayroll.model.Employee;
import com.erp.employeepayroll.model.Message;
import com.erp.employeepayroll.repository.EmployeeRepository;
import com.erp.employeepayroll.repository.MessageRepository;
import org.springframework.security.access.AccessDeniedException; // Import AccessDeniedException
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing messages sent to employees, especially for payroll notifications.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final EmployeeRepository employeeRepository;

    public MessageService(MessageRepository messageRepository, EmployeeRepository employeeRepository) {
        this.messageRepository = messageRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public void createPayslipApprovalMessage(Employee employee, Double amount, Integer month, Integer year) {
        String institutionName = "Government of Rwanda";
        String messageContent = String.format(
                "Dear %s, your salary for %d/%d from %s amounting to %.2f RWF has been credited to your account %s successfully.",
                employee.getFirstName(), month, year, institutionName, amount, employee.getCode()
        );

        Message message = Message.builder()
                .employee(employee)
                .message(messageContent)
                .month(month)
                .year(year)
                .sentDate(LocalDate.now())
                .build();

        messageRepository.save(message);

        System.out.println("DEBUG: Sending email to " + employee.getEmail() + ": " + messageContent);
    }

    public MessageResponse getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message id"+ id));

        // Get authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmployeeCode = authentication.getName();

        // Check if the authenticated user has ROLE_EMPLOYEE and is trying to access another employee's message
        boolean isEmployeeRole = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isEmployeeRole && !message.getEmployee().getCode().equals(authenticatedEmployeeCode)) {
            throw new AccessDeniedException("You are not authorized to access other employees' messages.");
        }

        return mapToResponse(message);
    }

    public List<MessageResponse> getMessagesByEmployee(Long employeeId) {
        // Get authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmployeeCode = authentication.getName();

        Employee requestedEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee id"+ employeeId));

        // Check if the authenticated user has ROLE_EMPLOYEE and is trying to access another employee's messages
        boolean isEmployeeRole = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isEmployeeRole && !requestedEmployee.getCode().equals(authenticatedEmployeeCode)) {
            throw new AccessDeniedException("You are not authorized to access other employees' messages.");
        }

        List<Message> messages = messageRepository.findByEmployee(requestedEmployee);
        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .employeeId(message.getEmployee().getId())
                .employeeCode(message.getEmployee().getCode())
                .employeeFirstName(message.getEmployee().getFirstName())
                .employeeLastName(message.getEmployee().getLastName())
                .messageContent(message.getMessage())
                .month(message.getMonth())
                .year(message.getYear())
                .sentDate(message.getSentDate())
                .build();
    }
}