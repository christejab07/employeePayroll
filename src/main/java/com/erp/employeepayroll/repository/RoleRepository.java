package com.erp.employeepayroll.repository;

import com.erp.employeepayroll.model.ERole;
import com.erp.employeepayroll.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Role entity.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Finds a role by its name.
     * @param name The name of the role (e.g., ROLE_EMPLOYEE).
     * @return An Optional containing the role if found.
     */
    Optional<Role> findByName(ERole name);
}