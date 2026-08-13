package com.saas.hrms.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.saas.hrms.dto.CompanyResponse;
import com.saas.hrms.dto.UpdatePlanRequest;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.SuperAdminService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Component
@Scope("view")
@RequiredArgsConstructor
@Getter
@Setter
public class SuperAdminBean {

    private final SuperAdminService superAdminService;
    private final SessionBean sessionBean;

    private List<CompanyResponse> companies = new ArrayList<>();

    private Long selectedCompanyId;
    private String selectedCompanyName;
    private String selectedPlanType;

    @PostConstruct
    public void init() {
        if (sessionBean.getPrincipal() == null) {
            return;
        }
        loadCompanies();
    }

    public void loadCompanies() {
        try {
            companies = superAdminService.getAllCompanies();
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void activate(Long companyId) {
        try {
            superAdminService.activateCompany(companyId);
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Company activated successfully", null));
            loadCompanies();
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void deactivate(Long companyId) {
        try {
            superAdminService.deactivateCompany(companyId);
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Company deactivated successfully", null));
            loadCompanies();
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void openPlanDialog(CompanyResponse company) {
        selectedCompanyId = company.getId();
        selectedCompanyName = company.getName();
        selectedPlanType = company.getPlanType();
    }

    public void updatePlan() {
        if (selectedPlanType == null || selectedPlanType.isBlank()) {
            addError("Please select a plan type");
            return;
        }
        UpdatePlanRequest request = new UpdatePlanRequest();
        request.setPlanType(selectedPlanType);
        try {
            superAdminService.updatePlanType(selectedCompanyId, request);
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Plan updated successfully", null));
            loadCompanies();
            selectedCompanyId = null;
            selectedCompanyName = null;
            selectedPlanType = null;
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
    
}