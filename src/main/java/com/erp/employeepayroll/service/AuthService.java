package com.erp.employeepayroll.service;

import com.erp.employeepayroll.dto.request.AuthRequest;
import com.erp.employeepayroll.dto.request.EmployeeRequest;
import com.erp.employeepayroll.dto.response.EmployeeResponse;
import com.erp.employeepayroll.dto.response.JwtAuthResponse;
import com.erp.employeepayroll.model.ERole;
import com.erp.employeepayroll.model.Employee;
import com.erp.employeepayroll.model.Role;
import com.erp.employeepayroll.repository.EmployeeRepository;
import com.erp.employeepayroll.repository.RoleRepository;
import com.erp.employeepayroll.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for handling user authentication and registration.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       EmployeeRepository employeeRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authenticates a user and generates a JWT token.
     * @param authRequest Authentication request containing email and password.
     * @return JwtAuthResponse with access token and user details.
     */
    public JwtAuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        // Get authenticated user details to return in response
        Employee authenticatedEmployee = employeeRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Authenticated employee not found. This should not happen."));

        String rolesString = authenticatedEmployee.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(","));

        return new JwtAuthResponse(token, authenticatedEmployee.getCode(), authenticatedEmployee.getEmail(), rolesString);
    }

    /**
     * Registers a new employee with default and specified roles.
     * @param employeeRequest EmployeeRequest DTO for registration.
     * @return EmployeeResponse DTO of the registered employee.
     * @throws RuntimeException if email or employee code already exists or roles are invalid.
     */
    @Transactional
    public EmployeeResponse registerEmployee(EmployeeRequest employeeRequest) {
        if (employeeRepository.existsByEmail(employeeRequest.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }
        if (employeeRepository.existsByCode(employeeRequest.getCode())) {
            throw new RuntimeException("Employee code is already taken!");
        }

        Employee employee = new Employee();
        employee.setCode(employeeRequest.getCode());
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setLastName(employeeRequest.getLastName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setPassword(passwordEncoder.encode(employeeRequest.getPassword())); // Hash password
        employee.setMobile(employeeRequest.getMobile());
        employee.setDateOfBirth(employeeRequest.getDateOfBirth());
        employee.setStatus(employeeRequest.getStatus()); // Should typically be ACTIVE upon registration

        Set<String> strRoles = employeeRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default role if none specified
            Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new RuntimeException("Error: Role ROLE_EMPLOYEE is not found."));
            roles.add(employeeRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_ADMIN is not found."));
                        roles.add(adminRole);
                        break;
                    case "ROLE_MANAGER":
                        Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_MANAGER is not found."));
                        roles.add(managerRole);
                        break;
                    case "ROLE_EMPLOYEE":
                        Role empRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_EMPLOYEE is not found."));
                        roles.add(empRole);
                        break;
                    default:
                        throw new RuntimeException("Error: Role " + role + " does not exist.");
                }
            });
        }
        employee.setRoles(roles);
        Employee savedEmployee = employeeRepository.save(employee);

        return mapToEmployeeResponse(savedEmployee);
    }

    // Helper method to map Employee entity to EmployeeResponse DTO
    private EmployeeResponse mapToEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .code(employee.getCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .mobile(employee.getMobile())
                .dateOfBirth(employee.getDateOfBirth())
                .status(employee.getStatus())
                .build();
    }
}