package com.saas.hrms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Boolean isActive;
    private String planType;
    private long employeeCount;
    private LocalDateTime createdAt;
    
}