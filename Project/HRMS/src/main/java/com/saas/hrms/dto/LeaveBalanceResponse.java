package com.saas.hrms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceResponse {
	
    private String leaveTypeName;
    private Integer totalDays;
    private Integer usedDays;
    private Integer remainingDays;
    private Integer year;
    
}