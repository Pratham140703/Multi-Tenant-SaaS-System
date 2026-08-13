package com.saas.hrms.security;

import com.saas.hrms.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String name;
    private final String email;
    private final String password;
    private final Long companyId;
    private final String companyName;
    private final boolean active;
    private final GrantedAuthority authority;

    public UserPrincipal(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.companyId = (user.getCompany() != null) ? user.getCompany().getId() : null;
        this.companyName = (user.getCompany() != null) ? user.getCompany().getName() : null;
        this.active = Boolean.TRUE.equals(user.getIsActive());
        this.authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}