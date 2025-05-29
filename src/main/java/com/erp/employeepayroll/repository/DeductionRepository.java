package com.erp.employeepayroll.repository;

import com.erp.employeepayroll.model.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Deduction entity.
 */
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    /**
     * Finds a deduction by its name.
     * @param deductionName The name of the deduction.
     * @return An Optional containing the deduction if found.
     */
    Optional<Deduction> findByDeductionName(String deductionName);

    /**
     * Checks if a deduction with the given name already exists.
     * @param deductionName The name to check.
     * @return True if a deduction with this name exists, false otherwise.
     */
    Boolean existsByDeductionName(String deductionName);

    Deduction findByCode(String code);
    boolean existsByCode(String code);

}