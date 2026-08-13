package com.saas.hrms.bean;

import com.saas.hrms.dto.DepartmentResponse;
import com.saas.hrms.dto.EmployeeRequest;
import com.saas.hrms.dto.EmployeeResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.DepartmentService;
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
public class EmployeeFormBean {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long departmentId;
    private String designation;
    private LocalDate joiningDate;
    private Double monthlySalary;
    private Long reportingManagerId;

    private List<DepartmentResponse> departments = new ArrayList<>();
    private List<EmployeeResponse> managers = new ArrayList<>();
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        try {
            departments = departmentService.getAllDepartments();
            managers = employeeService.getAllEmployees();
        } catch (BadRequestException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void loadEmployee() {
        if (employeeId == null) {
            editMode = false;
            joiningDate = LocalDate.now();
            return;
        }
        editMode = true;
        try {
            EmployeeResponse emp = employeeService.getEmployeeById(employeeId);
            firstName = emp.getFirstName();
            lastName = emp.getLastName();
            email = emp.getEmail();
            phone = emp.getPhone();
            designation = emp.getDesignation();
            joiningDate = emp.getJoiningDate();
            monthlySalary = emp.getMonthlySalary();
            for (DepartmentResponse d : departments) {
                if (d.getName().equals(emp.getDepartmentName())) {
                    departmentId = d.getId();
                    break;
                }
            }
        } catch (BadRequestException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public String save() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (firstName == null || firstName.isBlank()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "First name is required", null));
            return null;
        }
        if (lastName == null || lastName.isBlank()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Last name is required", null));
            return null;
        }
        if (departmentId == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Department is required", null));
            return null;
        }
        if (designation == null || designation.isBlank()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Designation is required", null));
            return null;
        }
        if (joiningDate == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Joining date is required", null));
            return null;
        }
        if (monthlySalary == null || monthlySalary <= 0) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Salary must be a positive number", null));
            return null;
        }

        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhone(phone);
        request.setDepartmentId(departmentId);
        request.setDesignation(designation);
        request.setJoiningDate(joiningDate);
        request.setMonthlySalary(monthlySalary);
        request.setReportingManagerId(reportingManagerId);
        if (employeeId != null) {
            EmployeeResponse existing = employeeService.getEmployeeById(employeeId);
            request.setEmail(existing.getEmail());
        } 
        else {
            if (email == null || email.isBlank()) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email is required", null));
                return null;
            }
            request.setEmail(email);
        }
        try {
            if (employeeId != null) {
                employeeService.updateEmployee(employeeId, request);
            } else {
                employeeService.addEmployee(request);
            }
            return "/employee/list?faces-redirect=true";
        } catch (BadRequestException e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        }
    }

    public String cancel() {
        return "/employee/list?faces-redirect=true";
    }
    
}