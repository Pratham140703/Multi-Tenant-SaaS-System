package com.saas.hrms.bean;

import com.saas.hrms.dto.EmployeeResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;

@Component
@RequestScope
@RequiredArgsConstructor
@Getter
@Setter
public class EmployeeListBean {

    private final EmployeeService employeeService;

    private List<EmployeeResponse> employees = new ArrayList<>();
    private List<EmployeeResponse> filteredEmployees;
    private String searchQuery;

    @PostConstruct
    public void init() {
        loadEmployees();
    }

    public void loadEmployees() {
        try {
            employees = employeeService.getAllEmployees();
        } catch (BadRequestException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void search() {
        if (searchQuery == null || searchQuery.isBlank()) {
            filteredEmployees = null;
            return;
        }
        String q = searchQuery.trim().toLowerCase();
        filteredEmployees = new ArrayList<>();
        for (EmployeeResponse e : employees) {
            boolean matches =
                (e.getFirstName() != null && e.getFirstName().toLowerCase().contains(q)) ||
                (e.getLastName() != null && e.getLastName().toLowerCase().contains(q)) ||
                (e.getEmail() != null && e.getEmail().toLowerCase().contains(q)) ||
                (e.getEmployeeCode() != null && e.getEmployeeCode().toLowerCase().contains(q)) ||
                (e.getDepartmentName() != null && e.getDepartmentName().toLowerCase().contains(q)) ||
                (e.getDesignation() != null && e.getDesignation().toLowerCase().contains(q));
            if (matches) {
                filteredEmployees.add(e);
            }
        }
    }

    public void clearSearch() {
        searchQuery = null;
        filteredEmployees = null;
    }

    public void deactivate(Long employeeId) {
        try {
            employeeService.deactivateEmployee(employeeId);
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee deactivated successfully", null));
            loadEmployees();
        } catch (BadRequestException e) {
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public List<EmployeeResponse> getDisplayList() {
        if (filteredEmployees != null) {
            return filteredEmployees;
        } 
        else {
            return employees;
        }
    }

    public String statusBadgeClass(String status) {
        if (status == null) {
            return "badge-inactive";
        }
        if (status.equals("ACTIVE")) {
            return "badge-active";
        } 
        else if (status.equals("INACTIVE")) {
            return "badge-inactive";
        }
        else if (status.equals("ON_LEAVE")) {
            return "badge-on-leave";
        } 
        else if (status.equals("TERMINATED")) {
            return "badge-terminated";
        }
        else {
            return "badge-inactive";
        }
    }
    
}