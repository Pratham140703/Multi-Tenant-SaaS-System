package com.saas.hrms.bean;

import com.saas.hrms.dto.LoginRequest;
import com.saas.hrms.exception.BadRequestException;
import com.saas.hrms.security.UserPrincipal;
import com.saas.hrms.service.AuthService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class LoginBean {

    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;
    private final SessionBean sessionBean;

    @Getter
    @Setter
    private LoginRequest loginRequest = new LoginRequest();

    public String login() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            UserPrincipal principal = authService.login(loginRequest);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            securityContextRepository.saveContext(context, request, response);

            return resolveRedirect(principal);
            
        } catch (BadRequestException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        } catch (AuthenticationException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email or password", null));
            return null;
        }
    }

    private String resolveRedirect(UserPrincipal principal) {
        String role = principal.getAuthority().getAuthority();
        if ("ROLE_SUPER_ADMIN".equals(role)) {
            return "/superadmin/dashboard.xhtml?faces-redirect=true";
        }
        if ("ROLE_EMPLOYEE".equals(role)) {
            return "/employee/my-dashboard.xhtml?faces-redirect=true";
        }
        return "/index.xhtml?faces-redirect=true";
    }
}