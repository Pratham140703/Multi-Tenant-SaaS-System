package com.saas.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Company email is required")
    @Email(message = "Invalid email format")
    private String companyEmail;

    @NotBlank(message = "Phone number is required")
    private String companyPhone;

    @NotBlank(message = "Admin name is required")
    private String adminName;

    @NotBlank(message = "Admin email is required")
    @Email(message = "Invalid email format")
    private String adminEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
}