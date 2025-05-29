package com.erp.employeepayroll.repository;

import com.erp.employeepayroll.model.Employee;
import com.erp.employeepayroll.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Payslip entity.
 */
public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    /**
     * Finds a payslip for a specific employee, month, and year.
     * Used to prevent duplicate payroll generation.
     * @param employee The employee entity.
     * @param month The month (1-12).
     * @param year The year.
     * @return An Optional containing the payslip if found.
     */
    Optional<Payslip> findByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);

    /**
     * Finds all payslips for a specific employee.
     * @param employee The employee entity.
     * @return A list of payslips for the given employee.
     */
    List<Payslip> findByEmployee(Employee employee);

    /**
     * Finds all payslips for a given month and year.
     * @param month The month (1-12).
     * @param year The year.
     * @return A list of payslips for the given month and year.
     */
    List<Payslip> findByMonthAndYear(Integer month, Integer year);
}
