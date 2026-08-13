package com.saas.hrms.repository;

import com.saas.hrms.entity.Attendance;
import com.saas.hrms.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    Optional<Attendance> findByIdAndCompanyId(Long id, Long companyId);

    List<Attendance> findByCompanyIdAndAttendanceDate(Long companyId, LocalDate attendanceDate);

    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(Long employeeId, LocalDate from, LocalDate to);

    List<Attendance> findByCompanyIdAndAttendanceDateBetween(Long companyId, LocalDate from, LocalDate to);

    boolean existsByEmployeeIdAndAttendanceDateAndStatus(Long employeeId, LocalDate attendanceDate, AttendanceStatus status);
    
}