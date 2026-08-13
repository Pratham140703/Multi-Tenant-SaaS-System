package com.saas.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.saas.hrms.enums.PlanType;

@Entity
@Table(name = "companies")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private PlanType planType = PlanType.FREE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}