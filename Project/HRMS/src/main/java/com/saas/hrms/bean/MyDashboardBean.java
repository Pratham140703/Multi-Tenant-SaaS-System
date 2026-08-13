package com.saas.hrms.bean;

import com.saas.hrms.dto.EmployeeDashboardResponse;
import com.saas.hrms.dto.EmployeeResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.DashboardService;
import com.saas.hrms.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
@Getter
@Setter
public class MyDashboardBean {

    private final DashboardService dashboardService;
    private final EmployeeService employeeService;
    private final SessionBean sessionBean;

    private EmployeeDashboardResponse dashboard;
    private EmployeeResponse currentEmployee;

    @PostConstruct
    public void init() {
        if (sessionBean.getPrincipal() == null) {
            return;
        }
        try {
            String email = sessionBean.getPrincipal().getEmail();
            currentEmployee = employeeService.getEmployeeByEmail(email);
            dashboard = dashboardService.getEmployeeDashboard(currentEmployee.getId(), email, false);
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public String getMonthName(int month) {
        String[] names = {"January", "February", "March", "April", "May", "June","July", "August", "September", "October", "November", "December"};
        if (month < 1 || month > 12) return String.valueOf(month);
        return names[month - 1];
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
    
}