package com.saas.hrms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollResponse {

    private Long id;
    private String employeeName;
    private String employeeCode;
    private Integer month;
    private Integer year;
    private Double monthlySalary;
    private Long presentDays;
    private Long absentDays;
    private Long halfDays;
    private Double deductionAmount;
    private Double netSalary;
    private Boolean isPaid;
    
}