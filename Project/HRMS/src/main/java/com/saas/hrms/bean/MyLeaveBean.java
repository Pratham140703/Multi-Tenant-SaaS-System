package com.saas.hrms.bean;

import com.saas.hrms.dto.EmployeeResponse;
import com.saas.hrms.dto.LeaveBalanceResponse;
import com.saas.hrms.dto.LeaveRequestDto;
import com.saas.hrms.dto.LeaveRequestResponse;
import com.saas.hrms.dto.LeaveTypeResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.EmployeeService;
import com.saas.hrms.service.LeaveService;
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
public class MyLeaveBean {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;
    private final SessionBean sessionBean;

    private EmployeeResponse currentEmployee;

    private List<LeaveTypeResponse> leaveTypes = new ArrayList<>();
    private List<LeaveRequestResponse> myLeaveRequests = new ArrayList<>();
    private List<LeaveBalanceResponse> myLeaveBalances = new ArrayList<>();

    private Long leaveTypeId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;

    @PostConstruct
    public void init() {
        if (sessionBean.getPrincipal() == null) {
            return;
        }
        try {
            String email = sessionBean.getPrincipal().getEmail();
            currentEmployee = employeeService.getEmployeeByEmail(email);
            leaveTypes = leaveService.getAllLeaveTypes();
            loadMyLeaveRequests();
            loadMyLeaveBalances();
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void loadMyLeaveRequests() {
        myLeaveRequests = new ArrayList<>();
        if (currentEmployee == null) {
            return;
        }
        List<LeaveRequestResponse> all = leaveService.getAllLeaveRequests();
        for (LeaveRequestResponse lr : all) {
            if (lr.getEmployeeCode() != null && lr.getEmployeeCode().equals(currentEmployee.getEmployeeCode())) {
                myLeaveRequests.add(lr);
            }
        }
    }

    public void loadMyLeaveBalances() {
        if (currentEmployee == null) {
            return;
        }
        try {
            myLeaveBalances = leaveService.getEmployeeLeaveBalance(currentEmployee.getId(), sessionBean.getPrincipal().getEmail(), false);
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void openApplyDialog() {
        leaveTypeId = null;
        fromDate = null;
        toDate = null;
        reason = null;
    }

    public void apply() {
        if (leaveTypeId == null) {
            addError("Please select a leave type");
            return;
        }
        if (fromDate == null || toDate == null) {
            addError("Please select from and to dates");
            return;
        }
        if (reason == null || reason.isBlank()) {
            addError("Reason is required");
            return;
        }
        LeaveRequestDto request = new LeaveRequestDto();
        request.setLeaveTypeId(leaveTypeId);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setReason(reason.trim());
        try {
            leaveService.applyLeave(request, sessionBean.getPrincipal().getEmail());
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Leave request submitted successfully", null));
            loadMyLeaveRequests();
            loadMyLeaveBalances();
            leaveTypeId = null;
            fromDate = null;
            toDate = null;
            reason = null;
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}