package com.saas.hrms.config;

import com.saas.hrms.entity.User;
import com.saas.hrms.enums.Role;
import com.saas.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.superadmin.email}")
    private String superAdminEmail;

    @Value("${app.superadmin.password}")
    private String superAdminPassword;

    @Override
    public void run(String... args) {

        boolean alreadyExists = userRepository.existsByEmail(superAdminEmail);
        if (alreadyExists) {
            return;
        }

        User superAdmin = User.builder()
                .name("Super Admin")
                .email(superAdminEmail)
                .password(passwordEncoder.encode(superAdminPassword))
                .role(Role.SUPER_ADMIN)
                .isActive(true)
                .company(null)
                .build();

        userRepository.save(superAdmin);

        System.out.println("Super Admin created with email: " + superAdminEmail);
    }
}