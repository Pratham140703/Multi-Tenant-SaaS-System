package com.saas.hrms.bean;

import com.saas.hrms.dto.AttendanceResponse;
import com.saas.hrms.dto.EmployeeResponse;
import com.saas.hrms.dto.ManualAttendanceRequest;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.AttendanceService;
import com.saas.hrms.service.EmployeeService;
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
public class AttendanceManagementBean {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    private List<AttendanceResponse> dailyAttendance = new ArrayList<>();
    private List<EmployeeResponse> employees = new ArrayList<>();

    private LocalDate selectedDate;

    private Long markEmployeeId;
    private LocalDate markDate;
    private String markStatus;

    @PostConstruct
    public void init() {
        selectedDate = LocalDate.now();
        loadDailyAttendance();
        try {
            employees = employeeService.getAllEmployees();
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void loadDailyAttendance() {
        if (selectedDate == null) {
            addError("Please select a date");
            return;
        }
        try {
            dailyAttendance = attendanceService.getDailyAttendance(selectedDate);
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void openMarkDialog() {
        markEmployeeId = null;
        markDate = selectedDate != null ? selectedDate : LocalDate.now();
        markStatus = null;
    }

    public void markAttendance() {
        if (markEmployeeId == null) {
            addError("Please select an employee");
            return;
        }
        if (markDate == null) {
            addError("Please select a date");
            return;
        }
        if (markStatus == null || markStatus.isBlank()) {
            addError("Please select a status");
            return;
        }
        ManualAttendanceRequest request = new ManualAttendanceRequest();
        request.setEmployeeId(markEmployeeId);
        request.setAttendanceDate(markDate);
        request.setStatus(markStatus);
        try {
            attendanceService.markManualAttendance(request);
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Attendance marked successfully", null));
            loadDailyAttendance();
            markEmployeeId = null;
            markDate = null;
            markStatus = null;
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
    
}