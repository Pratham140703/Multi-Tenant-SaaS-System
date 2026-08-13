package com.saas.hrms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HrDashboardResponse {

    private long totalEmployees;
    private long activeEmployees;
    private long pendingLeaveRequests;
    private double todayAttendancePercentage;

    private Integer payrollMonth;
    private Integer payrollYear;
    private Double monthlyPayrollCost;
    
}