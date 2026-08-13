package com.saas.hrms.service;

import com.saas.hrms.dto.CompanyResponse;
import com.saas.hrms.dto.UpdatePlanRequest;
import com.saas.hrms.entity.Company;
import com.saas.hrms.enums.PlanType;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.repository.CompanyRepository;
import com.saas.hrms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    public List<CompanyResponse> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        List<CompanyResponse> result = new ArrayList<>();
        for (Company company : companies) {
            result.add(mapToResponse(company));
        }
        return result;
    }

    public CompanyResponse getCompanyById(Long companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }
        return mapToResponse(company);
    }

    @Transactional
    public CompanyResponse activateCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }
        boolean alreadyActive = Boolean.TRUE.equals(company.getIsActive());
        if (alreadyActive) {
            throw new BadRequestException("Company is already active");
        }
        company.setIsActive(true);
        Company saved = companyRepository.save(company);
        return mapToResponse(saved);
    }

    @Transactional
    public CompanyResponse deactivateCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }
        boolean alreadyInactive = Boolean.FALSE.equals(company.getIsActive());
        if (alreadyInactive) {
            throw new BadRequestException("Company is already deactivated");
        }
        company.setIsActive(false);
        Company saved = companyRepository.save(company);
        return mapToResponse(saved);
    }

    @Transactional
    public CompanyResponse updatePlanType(Long companyId, UpdatePlanRequest request) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }
        PlanType planType;
        try {
            planType = PlanType.valueOf(request.getPlanType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid plan type. Use FREE, PRO or ENTERPRISE");
        }
        company.setPlanType(planType);
        Company saved = companyRepository.save(company);
        return mapToResponse(saved);
    }

    private CompanyResponse mapToResponse(Company company) {
        long employeeCount = employeeRepository.countByCompanyId(company.getId());

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .phone(company.getPhone())
                .isActive(company.getIsActive())
                .planType(company.getPlanType().name())
                .employeeCount(employeeCount)
                .createdAt(company.getCreatedAt())
                .build();
    }
    
}