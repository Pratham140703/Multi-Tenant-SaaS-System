package com.saas.hrms.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmployeeResponse {
	
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String departmentName;
    private String designation;
    private LocalDate joiningDate;
    private Double monthlySalary;
    private String status;
    private String reportingManagerName; 
    
}