package com.saas.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LeaveTypeRequest {

    @NotBlank(message = "Leave type name is required")
    private String name;

    private String description;

    @NotNull(message = "Max days per year is required")
    @Min(value = 1, message = "Must be at least 1 day")
    private Integer maxDaysPerYear;
    
}