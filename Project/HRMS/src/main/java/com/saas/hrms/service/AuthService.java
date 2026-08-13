package com.saas.hrms.service;

import com.saas.hrms.dto.LoginRequest;
import com.saas.hrms.dto.RegisterCompanyRequest;
import com.saas.hrms.entity.Company;
import com.saas.hrms.entity.User;
import com.saas.hrms.enums.PlanType;
import com.saas.hrms.enums.Role;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.repository.CompanyRepository;
import com.saas.hrms.repository.UserRepository;
import com.saas.hrms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserPrincipal registerCompany(RegisterCompanyRequest request) {
        boolean companyEmailTaken = companyRepository.existsByEmail(request.getCompanyEmail());
        if (companyEmailTaken) {
            throw new BadRequestException("Company with this email already exists");
        }

        boolean adminEmailTaken = userRepository.existsByEmail(request.getAdminEmail());
        if (adminEmailTaken) {
            throw new BadRequestException("User with this email already exists");
        }
        Company company = Company.builder()
                .name(request.getCompanyName())
                .email(request.getCompanyEmail())
                .phone(request.getCompanyPhone())
                .isActive(true)
                .planType(PlanType.FREE)
                .build();

        Company savedCompany = companyRepository.save(company);
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User adminUser = User.builder()
                .name(request.getAdminName())
                .email(request.getAdminEmail())
                .password(encodedPassword)
                .role(Role.HR_ADMIN)
                .isActive(true)
                .company(savedCompany)
                .build();
        User savedAdmin = userRepository.save(adminUser);

        return new UserPrincipal(savedAdmin);
    }

    public UserPrincipal login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authenticationManager.authenticate(authToken);
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            throw new BadRequestException("Invalid email or password");
        }
        if (!user.getIsActive()) {
            throw new BadRequestException("Your account has been deactivated. Contact admin.");
        }
        return new UserPrincipal(user);
    }
    
}