package com.saas.hrms.bean;

import com.saas.hrms.dto.HrDashboardResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.DashboardService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
@RequestScope
@RequiredArgsConstructor
@Getter
@Setter
public class DashboardBean {

    private final DashboardService dashboardService;

    private HrDashboardResponse dashboard;
    private int selectedMonth;
    private int selectedYear;

    @PostConstruct
    public void init() {
        LocalDate today = LocalDate.now();
        selectedMonth = today.getMonthValue();
        selectedYear = today.getYear();
        loadDashboard();
    }

    public void loadDashboard() {
        try {
            dashboard = dashboardService.getHrDashboard(selectedMonth, selectedYear);
        } 
        catch (BadRequestException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            dashboard = HrDashboardResponse.builder()
                .totalEmployees(0).activeEmployees(0)
                .pendingLeaveRequests(0).todayAttendancePercentage(0.0)
                .payrollMonth(selectedMonth).payrollYear(selectedYear)
                .monthlyPayrollCost(0.0)
                .build();
        }
    }

    public String getMonthName() {
        return Month.of(selectedMonth).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public String goToAddEmployee() {
        return "/employee/add?faces-redirect=true";
    }

    public String goToAttendance() {
        return "/attendance/mark?faces-redirect=true";
    }

    public String goToLeaves() {
        return "/leave/list?faces-redirect=true";
    }

    public String goToPayroll() {
        return "/payroll/generate?faces-redirect=true";
    }
    
}