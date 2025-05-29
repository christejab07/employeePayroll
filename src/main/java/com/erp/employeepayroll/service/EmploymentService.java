package com.erp.employeepayroll.service;

import com.erp.employeepayroll.dto.request.EmploymentRequest;
import com.erp.employeepayroll.dto.response.EmploymentResponse;
import com.erp.employeepayroll.exception.ResourceNotFoundException;
import com.erp.employeepayroll.model.Employee;
import com.erp.employeepayroll.model.Employment;
import com.erp.employeepayroll.repository.EmployeeRepository;
import com.erp.employeepayroll.repository.EmploymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Employee employment details.
 */
@Service
public class EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;

    public EmploymentService(EmploymentRepository employmentRepository, EmployeeRepository employeeRepository) {
        this.employmentRepository = employmentRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a new employment record for an employee.
     * Ensures an employee does not have multiple active employment records simultaneously.
     * @param request EmploymentRequest DTO.
     * @return EmploymentResponse DTO of the created employment.
     * @throws ResourceNotFoundException if the employee does not exist.
     * @throws RuntimeException if an active employment already exists for the employee or employment code is duplicate.
     */
    @Transactional
    public EmploymentResponse createEmployment(EmploymentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee id" + request.getEmployeeId()));

        if (employmentRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Employment with code '" + request.getCode() + "' already exists.");
        }

        // Check if there's already an active employment for this employee
        if (request.getStatus() == Employment.EmploymentStatus.ACTIVE &&
                employmentRepository.findByEmployeeAndStatus(employee, Employment.EmploymentStatus.ACTIVE).isPresent()) {
            throw new RuntimeException("Employee already has an ACTIVE employment record. Please deactivate existing one first.");
        }

        Employment employment = new Employment();
        employment.setCode(request.getCode());
        employment.setEmployee(employee);
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setStatus(request.getStatus());
        employment.setJoiningDate(request.getJoiningDate());

        Employment savedEmployment = employmentRepository.save(employment);
        return mapToResponse(savedEmployment);
    }

    /**
     * Retrieves an employment record by its ID.
     * @param id The ID of the employment record.
     * @return EmploymentResponse DTO.
     * @throws ResourceNotFoundException if employment record is not found.
     */
    public EmploymentResponse getEmploymentById(Long id) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment id"+id));
        return mapToResponse(employment);
    }

    /**
     * Retrieves all employment records.
     * @return List of EmploymentResponse DTOs.
     */
    public List<EmploymentResponse> getAllEmployments() {
        List<Employment> employments = employmentRepository.findAll();
        return employments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing employment record.
     * @param id The ID of the employment record to update.
     * @param request EmploymentRequest DTO with updated information.
     * @return EmploymentResponse DTO of the updated employment.
     * @throws ResourceNotFoundException if employment record or associated employee is not found.
     * @throws RuntimeException if an active employment already exists for the employee (if status changes to ACTIVE)
     * or employment code is duplicate for another record.
     */
    @Transactional
    public EmploymentResponse updateEmployment(Long id, EmploymentRequest request) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment id" + id));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee id " +request.getEmployeeId()));

        // Check if code changed and if new code is taken by another employment record
        if (!employment.getCode().equalsIgnoreCase(request.getCode()) && employmentRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Employment with code '" + request.getCode() + "' already exists for another record.");
        }

        // If trying to activate this employment, ensure no other active employment exists for this employee
        if (request.getStatus() == Employment.EmploymentStatus.ACTIVE) {
            employmentRepository.findByEmployeeAndStatus(employee, Employment.EmploymentStatus.ACTIVE)
                    .ifPresent(activeEmployment -> {
                        if (!activeEmployment.getId().equals(id)) { // If it's a different active employment
                            throw new RuntimeException("Employee already has another ACTIVE employment record. Deactivate it first.");
                        }
                    });
        }


        employment.setEmployee(employee); // Can update employee if needed, though usually fixed
        employment.setCode(request.getCode());
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setStatus(request.getStatus());
        employment.setJoiningDate(request.getJoiningDate());

        Employment updatedEmployment = employmentRepository.save(employment);
        return mapToResponse(updatedEmployment);
    }

    /**
     * Deletes an employment record by its ID.
     * @param id The ID of the employment record to delete.
     * @throws ResourceNotFoundException if employment record is not found.
     */
    @Transactional
    public void deleteEmployment(Long id) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment id"+ id));
        employmentRepository.delete(employment);
    }

    // Helper method to map Employment entity to EmploymentResponse DTO
    private EmploymentResponse mapToResponse(Employment employment) {
        return EmploymentResponse.builder()
                .id(employment.getId())
                .code(employment.getCode())
                .employeeId(employment.getEmployee().getId())
                .employeeCode(employment.getEmployee().getCode())
                .employeeFirstName(employment.getEmployee().getFirstName())
                .employeeLastName(employment.getEmployee().getLastName())
                .department(employment.getDepartment())
                .position(employment.getPosition())
                .baseSalary(employment.getBaseSalary())
                .status(employment.getStatus())
                .joiningDate(employment.getJoiningDate())
                .build();
    }
}