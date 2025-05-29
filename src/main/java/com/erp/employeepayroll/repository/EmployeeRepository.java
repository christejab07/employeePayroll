package com.erp.employeepayroll.repository;

import com.erp.employeepayroll.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Employee entity.
 * Provides standard CRUD operations and custom query methods.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Finds an employee by their email address.
     * @param email The email of the employee.
     * @return An Optional containing the employee if found, otherwise empty.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Checks if an employee exists with the given email address.
     * @param email The email to check.
     * @return True if an employee with this email exists, false otherwise.
     */
    Boolean existsByEmail(String email);

    /**
     * Checks if an employee exists with the given employee code.
     * @param code The employee code to check.
     * @return True if an employee with this code exists, false otherwise.
     */
    Boolean existsByCode(String code);

    /**
     * Finds an employee by their unique code.
     * @param code The code of the employee.
     * @return An Optional containing the employee if found, otherwise empty.
     */
    Optional<Employee> findByCode(String code);
}
