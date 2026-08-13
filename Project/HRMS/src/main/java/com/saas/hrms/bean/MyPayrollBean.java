package com.saas.hrms.bean;

import com.saas.hrms.dto.PayrollResponse;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.service.PayrollService;
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
public class MyPayrollBean {

    private final PayrollService payrollService;
    private final SessionBean sessionBean;
    private List<PayrollResponse> myPayrollList = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (sessionBean.getPrincipal() == null) {
            return;
        }
        try {
            myPayrollList = payrollService.getMyPayroll(sessionBean.getPrincipal().getEmail());
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