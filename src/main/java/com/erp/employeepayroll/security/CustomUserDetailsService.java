package com.erp.employeepayroll.security;

import com.erp.employeepayroll.model.Employee;
import com.erp.employeepayroll.model.Role;
import com.erp.employeepayroll.repository.EmployeeRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Loads user-specific data (Employee details) during the authentication process.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Locates the user based on the username (email in this case).
     *
     * @param email The email of the user to load.
     * @return A UserDetails object representing the authenticated user.
     * @throws UsernameNotFoundException if the user could not be found or has no granted authority.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find employee by email, which serves as the username
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + email));

        // Convert employee roles to Spring Security GrantedAuthorities
        return new org.springframework.security.core.userdetails.User(
                employee.getEmail(),
                employee.getPassword(),
                mapRolesToAuthorities(employee.getRoles())
        );
    }

    /**
     * Helper method to map set of Role entities to a collection of GrantedAuthority.
     * @param roles Set of roles associated with the employee.
     * @return Collection of GrantedAuthority objects.
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }
}