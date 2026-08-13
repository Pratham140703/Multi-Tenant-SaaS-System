package com.saas.hrms.repository;

import com.saas.hrms.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
	
    List<LeaveType> findByCompanyId(Long companyId);
    Optional<LeaveType> findByIdAndCompanyId(Long id, Long companyId);
    boolean existsByNameAndCompanyId(String name, Long companyId);
    
}