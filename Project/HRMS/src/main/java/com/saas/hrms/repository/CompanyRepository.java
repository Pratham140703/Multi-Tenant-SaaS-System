package com.saas.hrms.repository;

import com.saas.hrms.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
	
    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
    
}