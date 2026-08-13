package com.saas.hrms.config;

import com.saas.hrms.security.TenantContextFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final TenantContextFilter tenantContextFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .securityContext(context -> context.securityContextRepository(securityContextRepository()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().migrateSession()
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login.xhtml",
                    "/error.xhtml",
                    "/error",
                    "/jakarta.faces.resource/**",
                    "/javax.faces.resource/**",
                    "/resources/**"
                ).permitAll()
                .requestMatchers(
                    "/superadmin/**",
                    "/faces/superadmin/**"
                ).hasRole("SUPER_ADMIN")
                .requestMatchers(
                    "/employee/my-dashboard.xhtml", "/faces/employee/my-dashboard.xhtml",
                    "/leave/my-leave.xhtml", "/faces/leave/my-leave.xhtml",
                    "/attendance/my-attendance.xhtml","/faces/attendance/my-attendance.xhtml",
                    "/payroll/my-payroll.xhtml", "/faces/payroll/my-payroll.xhtml"
                ).hasRole("EMPLOYEE")
                .requestMatchers(
                    "/index.xhtml", "/faces/index.xhtml",
                    "/employee/**", "/faces/employee/**",
                    "/department/**", "/faces/department/**",
                    "/leave/**", "/faces/leave/**",
                    "/attendance/**", "/faces/attendance/**",
                    "/payroll/**", "/faces/payroll/**",
                    "/reports/**", "/faces/reports/**"
                ).hasAnyRole("HR_ADMIN", "HR_MANAGER")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->response.sendRedirect(request.getContextPath() + "/login.xhtml"))
                .accessDeniedHandler((request, response, accessDeniedException) -> response.sendRedirect(request.getContextPath() + "/error.xhtml"))
            )
            .authenticationProvider(authenticationProvider())
            .addFilterAfter(tenantContextFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable());

        return http.build();
    }
}