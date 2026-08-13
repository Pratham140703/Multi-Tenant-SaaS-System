package com.saas.hrms.service;

import com.saas.hrms.dto.DepartmentHeadcountResponse;
import com.saas.hrms.dto.MonthlyAttendanceReportResponse;
import com.saas.hrms.entity.Attendance;
import com.saas.hrms.entity.Department;
import com.saas.hrms.entity.Employee;
import com.saas.hrms.enums.AttendanceStatus;
import com.saas.hrms.enums.EmployeeStatus;
import com.saas.hrms.repository.AttendanceRepository;
import com.saas.hrms.repository.EmployeeRepository;
import com.saas.hrms.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public List<DepartmentHeadcountResponse> getDepartmentHeadcountReport() {
        Long companyId = TenantContext.getTenantId();
        List<Employee> employees = employeeRepository.findByCompanyId(companyId);

        Map<Long, String> departmentNames = new HashMap<>();
        Map<Long, Long> departmentCounts = new HashMap<>();

        for (Employee e : employees) {
            if (e.getStatus() != EmployeeStatus.ACTIVE) {
                continue;
            }
            Department department = e.getDepartment();
            boolean departmentInactive = (department.getIsActive() == null) || !department.getIsActive();
            if (departmentInactive) {
                continue;
            }
            Long deptId = department.getId();
            String deptName = department.getName();
            departmentNames.put(deptId, deptName);
            Long currentCount = departmentCounts.get(deptId);
            if (currentCount == null) {
                departmentCounts.put(deptId, 1L);
            } 
            else {
                departmentCounts.put(deptId, currentCount + 1);
            }
        }
        List<DepartmentHeadcountResponse> result = new ArrayList<>();
        for (Long deptId : departmentCounts.keySet()) {
            DepartmentHeadcountResponse row = DepartmentHeadcountResponse.builder()
                    .departmentId(deptId)
                    .departmentName(departmentNames.get(deptId))
                    .employeeCount(departmentCounts.get(deptId))
                    .build();
            result.add(row);
        }
        return result;
    }
    
    public List<MonthlyAttendanceReportResponse> getMonthlyAttendanceReport(Integer month, Integer year) {
        Long companyId = TenantContext.getTenantId();
        LocalDate today = LocalDate.now();
        int reportMonth;
        if (month != null) {
            reportMonth = month;
        }
        else {
            reportMonth = today.getMonthValue();
        }
        int reportYear;
        if (year != null) {
            reportYear = year;
        }
        else {
            reportYear = today.getYear();
        }
        LocalDate start = LocalDate.of(reportYear, reportMonth, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Attendance> records = attendanceRepository.findByCompanyIdAndAttendanceDateBetween(companyId, start, end);
        Map<Long, List<Attendance>> recordsByEmployee = new HashMap<>();
        for (Attendance a : records) {
            Long empId = a.getEmployee().getId();
            List<Attendance> list = recordsByEmployee.get(empId);
            if (list == null) {
                list = new ArrayList<>();
                recordsByEmployee.put(empId, list);
            }
            list.add(a);
        }
        List<MonthlyAttendanceReportResponse> result = new ArrayList<>();
        for (Long empId : recordsByEmployee.keySet()) {
            List<Attendance> employeeRecords = recordsByEmployee.get(empId);
            result.add(buildRow(employeeRecords));
        }
        return result;
    }

    private MonthlyAttendanceReportResponse buildRow(List<Attendance> records) {
        Employee employee = records.get(0).getEmployee();
        long present = 0;
        long absent = 0;
        long halfDay = 0;
        long onLeave = 0;

        for (Attendance a : records) {
            if (a.getStatus() == AttendanceStatus.PRESENT) {
                present++;
            }
            else if (a.getStatus() == AttendanceStatus.ABSENT) {
                absent++;
            } 
            else if (a.getStatus() == AttendanceStatus.HALF_DAY) {
                halfDay++;
            } 
            else if (a.getStatus() == AttendanceStatus.ON_LEAVE) {
                onLeave++;
            }
        }
        long totalMarked = records.size();
        double attendancePercentage = 0.0;
        if (totalMarked > 0) {
            attendancePercentage = ((present + 0.5 * halfDay) * 100.0) / totalMarked;
        }
        double roundedPercentage = Math.round(attendancePercentage * 100.0) / 100.0;
        String employeeName = employee.getFirstName() + " " + employee.getLastName();

        return MonthlyAttendanceReportResponse.builder()
                .employeeId(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .employeeName(employeeName)
                .presentDays(present)
                .absentDays(absent)
                .halfDays(halfDay)
                .onLeaveDays(onLeave)
                .attendancePercentage(roundedPercentage)
                .build();
    }
    
}