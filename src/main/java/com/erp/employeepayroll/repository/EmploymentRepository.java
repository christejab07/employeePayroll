package com.erp.employeepayroll.repository;

import com.erp.employeepayroll.model.Employee;
import com.erp.employeepayroll.model.Employment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Employment entity.
 */
public interface EmploymentRepository extends JpaRepository<Employment, Long> {

    /**
     * Finds an employment record by employee and status.
     * Useful for fetching the active employment of a specific employee.
     * @param employee The employee entity.
     * @param status The status of the employment (e.g., ACTIVE, INACTIVE).
     * @return An Optional containing the employment record if found.
     */
    Optional<Employment> findByEmployeeAndStatus(Employee employee, Employment.EmploymentStatus status);

    /**
     * Finds an employment record by its unique code.
     * @param code The code of the employment record.
     * @return An Optional containing the employment record if found.
     */
    Optional<Employment> findByCode(String code);

    Employment findByEmployeeCode(String employeeCode);

    List<Employment> findByStatus(Employment.EmploymentStatus employmentStatus);
}
