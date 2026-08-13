package com.saas.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payroll", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "month", "year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "monthly_salary", nullable = false)
    private Double monthlySalary;

    @Column(name = "present_days")
    private Long presentDays;

    @Column(name = "absent_days")
    private Long absentDays;

    @Column(name = "half_days")
    private Long halfDays;

    @Column(name = "deduction_amount")
    private Double deductionAmount;

    @Column(name = "net_salary")
    private Double netSalary;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Builder.Default
    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }
    
}