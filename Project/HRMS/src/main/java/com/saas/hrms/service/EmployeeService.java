package com.saas.hrms.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.saas.hrms.dto.EmployeeRequest;
import com.saas.hrms.dto.EmployeeResponse;
import com.saas.hrms.entity.Company;
import com.saas.hrms.entity.Department;
import com.saas.hrms.entity.Employee;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.repository.CompanyRepository;
import com.saas.hrms.repository.DepartmentRepository;
import com.saas.hrms.repository.EmployeeRepository;
import com.saas.hrms.util.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeResponse addEmployee(EmployeeRequest request) {
        Long companyId = TenantContext.getTenantId();
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }
        Department department = departmentRepository.findByIdAndCompanyId(request.getDepartmentId(), companyId).orElse(null);
        if (department == null) {
            throw new BadRequestException("Department not found");
        }
        boolean emailTaken = employeeRepository.existsByEmailAndCompanyId(request.getEmail(), companyId);
        if (emailTaken) {
            throw new BadRequestException("Employee with this email already exists");
        }
        long count = employeeRepository.countByCompanyId(companyId);
        String employeeCode = generateEmployeeCode(company.getName(), count + 1);
        Employee reportingManager = null;
        if (request.getReportingManagerId() != null) {
            reportingManager = employeeRepository.findByIdAndCompanyId(request.getReportingManagerId(), companyId).orElse(null);
            if (reportingManager == null) {
                throw new BadRequestException("Reporting manager not found in your company");
            }
        }

        Employee employee = Employee.builder()
                .employeeCode(employeeCode)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(department)
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .monthlySalary(request.getMonthlySalary())
                .status(com.saas.hrms.enums.EmployeeStatus.ACTIVE)
                .company(company)
                .reportingManager(reportingManager)
                .build();

        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    public List<EmployeeResponse> getAllEmployees() {
        Long companyId = TenantContext.getTenantId();
        List<Employee> employees = employeeRepository.findByCompanyId(companyId);
        List<EmployeeResponse> responses = new ArrayList<>();
        for (Employee employee : employees) {
            responses.add(mapToResponse(employee));
        }
        return responses;
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByIdAndCompanyId(id, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found");
        }
        return mapToResponse(employee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByIdAndCompanyId(id, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found");
        }
        Department department = departmentRepository.findByIdAndCompanyId(request.getDepartmentId(), companyId).orElse(null);
        if (department == null) {
            throw new BadRequestException("Department not found");
        }
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setPhone(request.getPhone());
        employee.setDepartment(department);
        employee.setDesignation(request.getDesignation());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setMonthlySalary(request.getMonthlySalary());

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    public String deactivateEmployee(Long id) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByIdAndCompanyId(id, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found");
        }
        employee.setStatus(com.saas.hrms.enums.EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
        
        return "Employee deactivated successfully";
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        String departmentName = null;
        if (employee.getDepartment() != null) {
            departmentName = employee.getDepartment().getName();
        }
        String reportingManagerName = null;
        Employee manager = employee.getReportingManager();
        if (manager != null) {
            reportingManagerName = manager.getFirstName() + " " + manager.getLastName();
        }

        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .departmentName(departmentName)
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .monthlySalary(employee.getMonthlySalary())
                .status(employee.getStatus().name())
                .reportingManagerName(reportingManagerName)
                .build();
    }

    private String generateEmployeeCode(String companyName, long count) {
        String prefix;
        if (companyName.length() >= 2) {
            prefix = companyName.substring(0, 2).toUpperCase();
        } 
        else {
            prefix = companyName.toUpperCase();
        }
        int year = LocalDate.now().getYear();
        return String.format("%s-%d-%03d", prefix, year, count);
    }
    
    public EmployeeResponse getEmployeeByEmail(String email) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByEmailAndCompanyId(email, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee record not found. Make sure you are registered as an employee.");
        }
        return mapToResponse(employee);
    }
 
}