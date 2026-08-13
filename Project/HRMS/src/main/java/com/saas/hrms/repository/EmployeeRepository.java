package com.saas.hrms.repository;

import com.saas.hrms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // CRITICAL: Always filter by companyId — this is what enforces multi-tenancy
    List<Employee> findByCompanyId(Long companyId);

    Optional<Employee> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByEmail(String email);

    long countByCompanyId(Long companyId);
    
    Optional<Employee> findByEmailAndCompanyId(String email, Long companyId);
    
    boolean existsByEmailAndCompanyId(String email, Long companyId);
    
}