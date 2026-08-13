package com.saas.hrms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyAttendanceReportResponse {

    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    private long presentDays;
    private long absentDays;
    private long halfDays;
    private long onLeaveDays;

    private double attendancePercentage;
    
}