package com.saas.hrms.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class LeaveRequestResponse {
	
    private Long id;
    private String employeeName;
    private String employeeCode;
    private String leaveTypeName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer totalDays;
    private String reason;
    private String status;
    private String managerComment;
    private LocalDateTime createdAt;
    
}