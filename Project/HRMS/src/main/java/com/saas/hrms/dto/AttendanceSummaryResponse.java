package com.saas.hrms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummaryResponse {

    private String employeeName;
    private String employeeCode;
    private Long presentDays;
    private Long absentDays;
    private Long halfDays;
    private Long onLeaveDays;
    private Double totalWorkingHours;
    
}