package com.saas.hrms.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployeeDashboardResponse {

    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    private List<LeaveBalanceResponse> leaveBalances;

    private Integer attendanceMonth;
    private Integer attendanceYear;
    private double attendancePercentageThisMonth;

    private PayrollResponse lastPayslip;
    
}