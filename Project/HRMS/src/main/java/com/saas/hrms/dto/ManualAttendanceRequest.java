package com.saas.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManualAttendanceRequest {

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Date is required")
    private LocalDate attendanceDate;

    @NotNull(message = "Status is required")
    private String status; // PRESENT, ABSENT, HALF_DAY, ON_LEAVE
    
}