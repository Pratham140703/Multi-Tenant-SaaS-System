package com.saas.hrms.repository;

import com.saas.hrms.entity.LeaveRequest;
import com.saas.hrms.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByCompanyId(Long companyId);

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    List<LeaveRequest> findByCompanyIdAndStatus(Long companyId, LeaveStatus status);

    Optional<LeaveRequest> findByIdAndCompanyId(Long id, Long companyId);

    // Check for overlapping leave requests
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
           "AND lr.status != 'CANCELLED' AND lr.status != 'REJECTED' " +
           "AND (lr.fromDate <= :toDate AND lr.toDate >= :fromDate)")
    List<LeaveRequest> findOverlappingLeaves(@Param("employeeId") Long employeeId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    
}