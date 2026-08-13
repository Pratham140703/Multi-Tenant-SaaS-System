package com.saas.hrms.security;

import com.saas.hrms.repository.CompanyRepository;
import com.saas.hrms.util.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    private final CompanyRepository companyRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
                Long companyId = principal.getCompanyId();

                if (companyId != null) {
                    boolean companyIsActive = companyRepository.findById(companyId)
                            .map(company -> Boolean.TRUE.equals(company.getIsActive()))
                            .orElse(false);

                    if (!companyIsActive) {
                        SecurityContextHolder.clearContext();
                        request.getSession().invalidate();
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                TenantContext.setTenantId(companyId);
            }

            filterChain.doFilter(request, response);
        } 
        finally {
            TenantContext.clear();
        }
    }
}