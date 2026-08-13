package com.saas.hrms.bean;

import com.saas.hrms.dto.LeaveTypeRequest;
import com.saas.hrms.dto.LeaveTypeResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.LeaveService;
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
public class LeaveTypeBean {

    private final LeaveService leaveService;
    private List<LeaveTypeResponse> leaveTypes = new ArrayList<>();

    private String name;
    private String description;
    private Integer maxDaysPerYear;

    @PostConstruct
    public void init() {
        loadLeaveTypes();
    }

    public void loadLeaveTypes() {
        try {
            leaveTypes = leaveService.getAllLeaveTypes();
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    public void openAddDialog() {
        name = null;
        description = null;
        maxDaysPerYear = null;
    }

    public void save() {
        if (name == null || name.isBlank()) {
            addError("Leave type name is required");
            return;
        }
        if (maxDaysPerYear == null || maxDaysPerYear < 1) {
            addError("Max days per year must be at least 1");
            return;
        }
        LeaveTypeRequest request = new LeaveTypeRequest();
        request.setName(name.trim());
        request.setDescription(description != null ? description.trim() : null);
        request.setMaxDaysPerYear(maxDaysPerYear);
        try {
            leaveService.createLeaveType(request);
            FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Leave type created successfully", null));
            loadLeaveTypes();
            name = null;
            description = null;
            maxDaysPerYear = null;
        } catch (BadRequestException e) {
            addError(e.getMessage());
        }
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}