package com.saas.hrms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeResponse {

    private Long id;
    private String name;
    private String description;
    private Integer maxDaysPerYear;
    private Boolean isActive;
    
}