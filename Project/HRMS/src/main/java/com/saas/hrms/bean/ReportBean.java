package com.saas.hrms.bean;

import com.saas.hrms.dto.DepartmentHeadcountResponse;
import com.saas.hrms.dto.MonthlyAttendanceReportResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.ReportService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequestScope
@RequiredArgsConstructor
@Getter
@Setter
public class ReportBean {

    private final ReportService reportService;
    private final SessionBean sessionBean;

    private List<DepartmentHeadcountResponse> headcountReport = new ArrayList<>();
    private List<MonthlyAttendanceReportResponse> attendanceReport = new ArrayList<>();

    private Integer filterMonth;
    private Integer filterYear;

    @PostConstruct
    public void init() {
        if (sessionBean.getPrincipal() == null) {
            return;
        }
        filterMonth = LocalDate.now().getMonthValue();
        filterYear = LocalDate.now().getYear();
        loadHeadcountReport();
        loadAttendanceReport();
    }

    public void loadHeadcountReport() {
        try {
            headcountReport = reportService.getDepartmentHeadcountReport();
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void loadAttendanceReport() {
        try {
            attendanceReport = reportService.getMonthlyAttendanceReport(filterMonth, filterYear);
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void applyFilter() {
        loadAttendanceReport();
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