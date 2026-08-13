package com.saas.hrms.bean;

import com.saas.hrms.security.UserPrincipal;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Getter
public class SessionBean {

    public UserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal up) {
            return up;
        }
        return null;
    }

    public String getDisplayName() {
        UserPrincipal up = getPrincipal();
        return (up != null) ? up.getName() : "Guest";
    }
    
    public String getCompanyName() {
        UserPrincipal up = getPrincipal();
        if (up == null) return "HRMS";
        if (up.getCompanyName() != null) return up.getCompanyName();
        return "HRMS";
    }
    
    public boolean isSuperAdmin() {
        UserPrincipal up = getPrincipal();
        return up != null && up.getAuthority().getAuthority().equals("ROLE_SUPER_ADMIN");
    }

    public boolean isHrAdmin() {
        UserPrincipal up = getPrincipal();
        return up != null && up.getAuthority().getAuthority().equals("ROLE_HR_ADMIN");
    }

    public boolean isHrManager() {
        UserPrincipal up = getPrincipal();
        return up != null && up.getAuthority().getAuthority().equals("ROLE_HR_MANAGER");
    }

    public boolean isEmployee() {
        UserPrincipal up = getPrincipal();
        return up != null && up.getAuthority().getAuthority().equals("ROLE_EMPLOYEE");
    }

    public String logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "/login.xhtml?faces-redirect=true";
    }
    
}