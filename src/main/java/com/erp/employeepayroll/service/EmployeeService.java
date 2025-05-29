package com.erp.employeepayroll.service;

import com.erp.employeepayroll.dto.request.EmployeeRequest;
import com.erp.employeepayroll.dto.response.EmployeeResponse;
import com.erp.employeepayroll.exception.ResourceNotFoundException;
import com.erp.employeepayroll.model.ERole;
import com.erp.employeepayroll.model.Employee;
import com.erp.employeepayroll.model.Role;
import com.erp.employeepayroll.repository.EmployeeRepository;
import com.erp.employeepayroll.repository.RoleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing Employee personal information.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new employee record.
     * Note: For initial registration, use AuthService.registerEmployee to handle default roles.
     * This method is for admin/manager adding employees.
     * @param request EmployeeRequest DTO.
     * @return EmployeeResponse DTO of the created employee.
     * @throws RuntimeException if email or code already exists or roles are invalid.
     */
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Employee with this email already exists: " + request.getEmail());
        }
        if (employeeRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Employee with this code already exists: " + request.getCode());
        }

        Employee employee = new Employee();
        employee.setCode(request.getCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        // For new employee creation, password is required and must be encoded
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            throw new IllegalArgumentException("Password is required for new employee creation.");
        }
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setStatus(request.getStatus());

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            request.getRoles().forEach(roleName -> {
                ERole eRole = ERole.valueOf(roleName.toUpperCase()); // Convert string to ERole enum
                Role role = roleRepository.findByName(eRole)
                        .orElseThrow(() -> new ResourceNotFoundException("Role with name" + roleName + " not found in database."));
                roles.add(role);
            });
        } else {
            // Assign default EMPLOYEE role if none specified (or decide to throw error)
            Role defaultRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            roles.add(defaultRole);
        }
        employee.setRoles(roles);

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }

    /**
     * Retrieves an employee by their ID.
     * @param id The ID of the employee.
     * @return EmployeeResponse DTO.
     * @throws ResourceNotFoundException if employee is not found.
     */
    public EmployeeResponse getEmployeeById(Long id) throws AccessDeniedException {
        // Get authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmployeeCode = authentication.getName(); // This is the employee's code (username)

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee id"+ id));

        // Check if the authenticated user has ROLE_EMPLOYEE and is trying to access another employee's data
        boolean isEmployeeRole = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isEmployeeRole && !employee.getCode().equals(authenticatedEmployeeCode)) {
            throw new AccessDeniedException("You are not authorized to access other employees' data.");
        }

        return mapToResponse(employee);
    }

    /**
     * Retrieves an employee by their code.
     * @param code The code of the employee.
     * @return EmployeeResponse DTO.
     * @throws ResourceNotFoundException if employee is not found.
     */
    public EmployeeResponse getEmployeeByCode(String code) throws AccessDeniedException {
        // Get authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmployeeCode = authentication.getName();

        Employee employee = employeeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Employee code"+ code));

        // Check if the authenticated user has ROLE_EMPLOYEE and is trying to access another employee's data
        boolean isEmployeeRole = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isEmployeeRole && !employee.getCode().equals(authenticatedEmployeeCode)) {
            throw new AccessDeniedException("You are not authorized to access other employees' data.");
        }

        return mapToResponse(employee);
    }

    /**
     * Retrieves all employees.
     * @return List of EmployeeResponse DTOs.
     */
    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing employee's details.
     * @param id The ID of the employee to update.
     * @param request EmployeeRequest DTO with updated information.
     * @return EmployeeResponse DTO of the updated employee.
     * @throws ResourceNotFoundException if employee is not found.
     * @throws RuntimeException if email or code already exists for another employee.
     */
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee id"+ id));

        // Check if email changed and if new email is taken by another employee
        if (!employee.getEmail().equalsIgnoreCase(request.getEmail()) && employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken by another employee: " + request.getEmail());
        }
        // Check if code changed and if new code is taken by another employee
        if (!employee.getCode().equalsIgnoreCase(request.getCode()) && employeeRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Employee code is already taken by another employee: " + request.getCode());
        }

        employee.setCode(request.getCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        // Only update password if a new one is provided in the request
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setStatus(request.getStatus());

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            request.getRoles().forEach(roleName -> {
                ERole eRole = ERole.valueOf(roleName.toUpperCase());
                Role role = roleRepository.findByName(eRole)
                        .orElseThrow(() -> new ResourceNotFoundException("Role name"+roleName));
                roles.add(role);
            });
        }
        employee.setRoles(roles);

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponse(updatedEmployee);
    }

    /**
     * Deletes an employee by their ID (sets status to DISABLED).
     * @param id The ID of the employee to delete.
     * @throws ResourceNotFoundException if employee is not found.
     */
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee" + id));
        employee.setStatus(Employee.EmployeeStatus.DISABLED); // Soft delete
        employeeRepository.save(employee);
        // Alternatively, for hard delete: employeeRepository.delete(employee);
    }

    // Helper method to map Employee entity to EmployeeResponse DTO
    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .code(employee.getCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .mobile(employee.getMobile())
                .dateOfBirth(employee.getDateOfBirth())
                .status(employee.getStatus())
                .roles(employee.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()))
                .build();
    }
}