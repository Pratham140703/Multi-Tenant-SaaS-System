package com.saas.hrms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {
	
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    
}