package com.saas.hrms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.saas.hrms.dto.DepartmentRequest;
import com.saas.hrms.dto.DepartmentResponse;
import com.saas.hrms.entity.Company;
import com.saas.hrms.entity.Department;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.repository.CompanyRepository;
import com.saas.hrms.repository.DepartmentRepository;
import com.saas.hrms.util.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        Long companyId = TenantContext.getTenantId();
        boolean nameTaken = departmentRepository.existsByNameAndCompanyId(request.getName(), companyId);
        if (nameTaken) {
            throw new BadRequestException("Department with this name already exists");
        }
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }
        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .company(company)
                .build();

        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    public List<DepartmentResponse> getAllDepartments() {
        Long companyId = TenantContext.getTenantId();
        List<Department> departments = departmentRepository.findByCompanyId(companyId);
        List<DepartmentResponse> responses = new ArrayList<>();
        for (Department department : departments) {
            responses.add(mapToResponse(department));
        }
        return responses;
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Long companyId = TenantContext.getTenantId();
        Department department = departmentRepository.findByIdAndCompanyId(id, companyId).orElse(null);
        if (department == null) {
            throw new BadRequestException("Department not found");
        }
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    public String deleteDepartment(Long id) {
        Long companyId = TenantContext.getTenantId();
        Department department = departmentRepository.findByIdAndCompanyId(id, companyId).orElse(null);
        if (department == null) {
            throw new BadRequestException("Department not found");
        }
        department.setIsActive(false);
        departmentRepository.save(department);
        return "Department deactivated successfully";
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .isActive(department.getIsActive())
                .build();
    }
    
}