package com.saas.hrms.repository;

import com.saas.hrms.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    List<Payroll> findByCompanyIdAndMonthAndYear(Long companyId, Integer month, Integer year);

    Optional<Payroll> findByIdAndCompanyId(Long id, Long companyId);

    List<Payroll> findByEmployeeId(Long employeeId);
    
}