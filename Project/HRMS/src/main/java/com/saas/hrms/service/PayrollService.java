package com.saas.hrms.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saas.hrms.dto.GeneratePayrollRequest;
import com.saas.hrms.dto.PayrollResponse;
import com.saas.hrms.entity.Attendance;
import com.saas.hrms.entity.Company;
import com.saas.hrms.entity.Employee;
import com.saas.hrms.entity.Payroll;
import com.saas.hrms.enums.AttendanceStatus;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.repository.AttendanceRepository;
import com.saas.hrms.repository.CompanyRepository;
import com.saas.hrms.repository.EmployeeRepository;
import com.saas.hrms.repository.PayrollRepository;
import com.saas.hrms.util.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public List<PayrollResponse> generatePayroll(GeneratePayrollRequest request) {
        Long companyId = TenantContext.getTenantId();
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }

        List<Employee> employees = employeeRepository.findByCompanyId(companyId);
        LocalDate from = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        int daysInMonth = YearMonth.of(request.getYear(), request.getMonth()).lengthOfMonth();

        List<PayrollResponse> results = new java.util.ArrayList<>();
        for (Employee employee : employees) {
            List<Attendance> records = attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employee.getId(), from, to);

            long presentDays = 0;
            long absentDays = 0;
            long halfDays = 0;
            for (Attendance a : records) {
                if (a.getStatus() == AttendanceStatus.PRESENT) {
                    presentDays++;
                }
                else if (a.getStatus() == AttendanceStatus.ABSENT) {
                    absentDays++;
                } 
                else if (a.getStatus() == AttendanceStatus.HALF_DAY) {
                    halfDays++;
                }
            }

            double perDayRate = employee.getMonthlySalary() / daysInMonth;
            double deduction = perDayRate * (absentDays + halfDays * 0.5);
            double netSalary = employee.getMonthlySalary() - deduction;
            
            Payroll payroll = payrollRepository.findByEmployeeIdAndMonthAndYear(employee.getId(), request.getMonth(), request.getYear()).orElse(null);
            if (payroll == null) {
                payroll = Payroll.builder()
                        .employee(employee)
                        .company(company)
                        .month(request.getMonth())
                        .year(request.getYear())
                        .build();
            }
            payroll.setMonthlySalary(employee.getMonthlySalary());
            payroll.setPresentDays(presentDays);
            payroll.setAbsentDays(absentDays);
            payroll.setHalfDays(halfDays);
            payroll.setDeductionAmount(Math.round(deduction * 100.0) / 100.0);
            payroll.setNetSalary(Math.round(netSalary * 100.0) / 100.0);

            Payroll saved = payrollRepository.save(payroll);
            results.add(mapToResponse(saved));
        }

        return results;
    }
    
    public List<PayrollResponse> getCompanyPayroll(Integer month, Integer year) {
        Long companyId = TenantContext.getTenantId();
        List<Payroll> payrolls = payrollRepository.findByCompanyIdAndMonthAndYear(companyId, month, year);
        List<PayrollResponse> responses = new ArrayList<>();
        for (Payroll p : payrolls) {
            responses.add(mapToResponse(p));
        }
        return responses;
    }

    public List<PayrollResponse> getMyPayroll(String email) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByEmailAndCompanyId(email, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found");
        }
        List<Payroll> payrolls = payrollRepository.findByEmployeeId(employee.getId());
        List<PayrollResponse> responses = new ArrayList<>();
        for (Payroll p : payrolls) {
            responses.add(mapToResponse(p));
        }
        return responses;
    }

    @Transactional
    public PayrollResponse markAsPaid(Long payrollId) {
        Long companyId = TenantContext.getTenantId();
        Payroll payroll = payrollRepository.findByIdAndCompanyId(payrollId, companyId).orElse(null);
        if (payroll == null) {
            throw new BadRequestException("Payroll record not found");
        }
        payroll.setIsPaid(true);
        Payroll saved = payrollRepository.save(payroll);
        return mapToResponse(saved);
    }

    private PayrollResponse mapToResponse(Payroll p) {
        String employeeName = p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName();

        return PayrollResponse.builder()
                .id(p.getId())
                .employeeName(employeeName)
                .employeeCode(p.getEmployee().getEmployeeCode())
                .month(p.getMonth())
                .year(p.getYear())
                .monthlySalary(p.getMonthlySalary())
                .presentDays(p.getPresentDays())
                .absentDays(p.getAbsentDays())
                .halfDays(p.getHalfDays())
                .deductionAmount(p.getDeductionAmount())
                .netSalary(p.getNetSalary())
                .isPaid(p.getIsPaid())
                .build();
    }
    
}