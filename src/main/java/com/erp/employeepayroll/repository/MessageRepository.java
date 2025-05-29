package com.erp.employeepayroll.repository;

import com.erp.employeepayroll.model.Employee;
import com.erp.employeepayroll.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Spring Data JPA repository for the Message entity.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Finds all messages for a specific employee.
     * @param employee The employee entity.
     * @return A list of messages for the given employee.
     */
    List<Message> findByEmployee(Employee employee);
}
