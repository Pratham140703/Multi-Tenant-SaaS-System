package com.saas.hrms.bean;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.saas.hrms.dto.CompanyResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.SuperAdminService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Component
@RequestScope
@RequiredArgsConstructor
@Getter
@Setter
public class SuperAdminDashboardBean {

    private final SuperAdminService superAdminService;
    private final SessionBean sessionBean;

    private int totalCompanies;
    private int activeCompanies;
    private int inactiveCompanies;
    private long totalEmployees;

    @PostConstruct
    public void init() {
        if (sessionBean.getPrincipal() == null) {
            return;
        }
        try {
            List<CompanyResponse> companies = superAdminService.getAllCompanies();
            totalCompanies = companies.size();
            activeCompanies = 0;
            inactiveCompanies = 0;
            totalEmployees = 0;
            for (CompanyResponse c : companies) {
                if (Boolean.TRUE.equals(c.getIsActive())) {
                    activeCompanies++;
                } 
                else {
                    inactiveCompanies++;
                }
                totalEmployees += c.getEmployeeCount();
            }
        } catch (BadRequestException e) {
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public String goToCompanies() {
        return "/superadmin/companies.xhtml?faces-redirect=true";
    }
    
}