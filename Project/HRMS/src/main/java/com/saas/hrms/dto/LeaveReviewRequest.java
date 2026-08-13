package com.saas.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveReviewRequest {

    @NotBlank(message = "Decision is required")
    private String decision; // "APPROVE" or "REJECT"

    private String comment;
    
}