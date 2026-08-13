package com.saas.hrms.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saas.hrms.dto.LeaveBalanceResponse;
import com.saas.hrms.dto.LeaveRequestDto;
import com.saas.hrms.dto.LeaveRequestResponse;
import com.saas.hrms.dto.LeaveReviewRequest;
import com.saas.hrms.dto.LeaveTypeRequest;
import com.saas.hrms.dto.LeaveTypeResponse;
import com.saas.hrms.entity.Company;
import com.saas.hrms.entity.Employee;
import com.saas.hrms.entity.LeaveBalance;
import com.saas.hrms.entity.LeaveRequest;
import com.saas.hrms.entity.LeaveType;
import com.saas.hrms.entity.User;
import com.saas.hrms.enums.LeaveStatus;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.repository.CompanyRepository;
import com.saas.hrms.repository.EmployeeRepository;
import com.saas.hrms.repository.LeaveBalanceRepository;
import com.saas.hrms.repository.LeaveRequestRepository;
import com.saas.hrms.repository.LeaveTypeRepository;
import com.saas.hrms.repository.UserRepository;
import com.saas.hrms.util.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public LeaveTypeResponse createLeaveType(LeaveTypeRequest request) {
        Long companyId = TenantContext.getTenantId();
        boolean nameExists = leaveTypeRepository.existsByNameAndCompanyId(request.getName(), companyId);
        if (nameExists) {
            throw new BadRequestException("Leave type already exists");
        }
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }
        LeaveType leaveType = LeaveType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .maxDaysPerYear(request.getMaxDaysPerYear())
                .company(company)
                .build();
        leaveTypeRepository.save(leaveType);
        
        return LeaveTypeResponse.builder()
                .id(leaveType.getId())
                .name(leaveType.getName())
                .description(leaveType.getDescription())
                .maxDaysPerYear(leaveType.getMaxDaysPerYear())
                .isActive(leaveType.getIsActive())
                .build();
    }

    public List<LeaveTypeResponse> getAllLeaveTypes() {
        Long companyId = TenantContext.getTenantId();
        List<LeaveType> leaveTypes = leaveTypeRepository.findByCompanyId(companyId);
        List<LeaveTypeResponse> responses = new ArrayList<>();
        for (LeaveType lt : leaveTypes) {
            LeaveTypeResponse response = LeaveTypeResponse.builder()
                    .id(lt.getId())
                    .name(lt.getName())
                    .description(lt.getDescription())
                    .maxDaysPerYear(lt.getMaxDaysPerYear())
                    .isActive(lt.getIsActive())
                    .build();
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public LeaveRequestResponse applyLeave(LeaveRequestDto request, String email) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByEmailAndCompanyId(email, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee record not found. Make sure you are registered as an employee.");
        }
        LeaveType leaveType = leaveTypeRepository.findByIdAndCompanyId(request.getLeaveTypeId(), companyId).orElse(null);
        if (leaveType == null) {
            throw new BadRequestException("Leave type not found");
        }
        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new BadRequestException("From date cannot be after to date");
        }
        if (request.getFromDate().isBefore(java.time.LocalDate.now())) {
            throw new BadRequestException("Cannot apply leave for past dates");
        }
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingLeaves(employee.getId(), request.getFromDate(), request.getToDate());
        if (!overlapping.isEmpty()) {
            throw new BadRequestException("You already have a leave request for these dates");
        }
        long totalDays = request.getFromDate().datesUntil(request.getToDate().plusDays(1)).count();
        int year = request.getFromDate().getYear();

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employee.getId(), leaveType.getId(), year).orElse(null);
        if (balance == null) {
            throw new BadRequestException("No leave balance found. Contact HR to allocate leaves.");
        }
        if (balance.getRemainingDays() < totalDays) {
            throw new BadRequestException("Insufficient leave balance. Available: " + balance.getRemainingDays() + " days");
        }
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found");
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveType(leaveType)
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .totalDays((int) totalDays)
                .reason(request.getReason())
                .company(company)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return mapToResponse(saved);
    }

    @Transactional
    public LeaveRequestResponse reviewLeave(Long leaveRequestId, LeaveReviewRequest request, String reviewerEmail) {
        Long companyId = TenantContext.getTenantId();
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdAndCompanyId(leaveRequestId, companyId).orElse(null);
        if (leaveRequest == null) {
            throw new BadRequestException("Leave request not found");
        }
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("This leave request has already been reviewed");
        }
        User reviewer = userRepository.findByEmail(reviewerEmail).orElse(null);
        if (reviewer == null) {
            throw new BadRequestException("Reviewer not found");
        }
        
        String decision = request.getDecision();
        if (decision.equalsIgnoreCase("APPROVE")) {
            leaveRequest.setStatus(LeaveStatus.APPROVED);
            int year = leaveRequest.getFromDate().getYear();
            LeaveBalance balance = leaveBalanceRepository
            		.findByEmployeeIdAndLeaveTypeIdAndYear(leaveRequest.getEmployee().getId(),leaveRequest.getLeaveType().getId(), year).orElse(null);
            if (balance == null) {
                throw new BadRequestException("Leave balance not found");
            }
            if (balance.getRemainingDays() < leaveRequest.getTotalDays()) {
                throw new BadRequestException("Cannot approve — insufficient balance remaining. Available: " + balance.getRemainingDays() + " days");
            }

            balance.setUsedDays(balance.getUsedDays() + leaveRequest.getTotalDays());
            balance.setRemainingDays(balance.getRemainingDays() - leaveRequest.getTotalDays());
            leaveBalanceRepository.save(balance);
        }
        else if (decision.equalsIgnoreCase("REJECT")) {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
        } 
        else {
            throw new BadRequestException("Decision must be APPROVE or REJECT");
        }

        leaveRequest.setManagerComment(request.getComment());
        leaveRequest.setReviewedBy(reviewer);
        leaveRequest.setReviewedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return mapToResponse(saved);
    }

    public List<LeaveRequestResponse> getAllLeaveRequests() {
        Long companyId = TenantContext.getTenantId();
        List<LeaveRequest> requests = leaveRequestRepository.findByCompanyId(companyId);
        List<LeaveRequestResponse> responses = new ArrayList<>();
        for (LeaveRequest lr : requests) {
            responses.add(mapToResponse(lr));
        }
        return responses;
    }

    public List<LeaveRequestResponse> getPendingLeaveRequests() {
        Long companyId = TenantContext.getTenantId();
        List<LeaveRequest> requests = leaveRequestRepository.findByCompanyIdAndStatus(companyId, LeaveStatus.PENDING);
        List<LeaveRequestResponse> responses = new ArrayList<>();
        for (LeaveRequest lr : requests) {
            responses.add(mapToResponse(lr));
        }
        return responses;
    }

    @Transactional
    public String allocateLeaveBalance(Long employeeId, Long leaveTypeId, Integer days, Integer year) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found");
        }
        LeaveType leaveType = leaveTypeRepository.findByIdAndCompanyId(leaveTypeId, companyId).orElse(null);
        if (leaveType == null) {
            throw new BadRequestException("Leave type not found");
        }
        LeaveBalance existing = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year).orElse(null);
        if (existing != null) {
            existing.setTotalDays(days);
            existing.setRemainingDays(days - existing.getUsedDays());
            leaveBalanceRepository.save(existing);
        } 
        else {
            LeaveBalance balance = LeaveBalance.builder()
                    .employee(employee)
                    .leaveType(leaveType)
                    .year(year)
                    .totalDays(days)
                    .usedDays(0)
                    .remainingDays(days)
                    .build();
            leaveBalanceRepository.save(balance);
        }
        return "Leave balance allocated successfully";
    }

    public List<LeaveBalanceResponse> getEmployeeLeaveBalance(Long employeeId) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found");
        }
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeId(employeeId);
        List<LeaveBalanceResponse> responses = new ArrayList<>();
        for (LeaveBalance b : balances) {
            LeaveBalanceResponse response = LeaveBalanceResponse.builder()
                    .leaveTypeName(b.getLeaveType().getName())
                    .totalDays(b.getTotalDays())
                    .usedDays(b.getUsedDays())
                    .remainingDays(b.getRemainingDays())
                    .year(b.getYear())
                    .build();
            responses.add(response);
        }
        return responses;
    }

    private LeaveRequestResponse mapToResponse(LeaveRequest lr) {
        String employeeName = lr.getEmployee().getFirstName() + " " + lr.getEmployee().getLastName();

        return LeaveRequestResponse.builder()
                .id(lr.getId())
                .employeeName(employeeName)
                .employeeCode(lr.getEmployee().getEmployeeCode())
                .leaveTypeName(lr.getLeaveType().getName())
                .fromDate(lr.getFromDate())
                .toDate(lr.getToDate())
                .totalDays(lr.getTotalDays())
                .reason(lr.getReason())
                .status(lr.getStatus().name())
                .managerComment(lr.getManagerComment())
                .createdAt(lr.getCreatedAt())
                .build();
    }
    
    public List<LeaveBalanceResponse> getEmployeeLeaveBalance(Long employeeId, String requesterEmail, boolean isHr) {
        Long companyId = TenantContext.getTenantId();
        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found");
        }
        boolean isOwnRecord = employee.getEmail().equalsIgnoreCase(requesterEmail);
        if (!isHr && !isOwnRecord) {
            throw new BadRequestException("You are not allowed to view this employee's leave balance");
        }
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeId(employeeId);
        List<LeaveBalanceResponse> responses = new ArrayList<>();
        for (LeaveBalance b : balances) {
            LeaveBalanceResponse response = LeaveBalanceResponse.builder()
                    .leaveTypeName(b.getLeaveType().getName())
                    .totalDays(b.getTotalDays())
                    .usedDays(b.getUsedDays())
                    .remainingDays(b.getRemainingDays())
                    .year(b.getYear())
                    .build();
            responses.add(response);
        }
        return responses;
    }
    
}