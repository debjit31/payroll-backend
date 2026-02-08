package com.example.payroll_backend.auth.security;

import com.example.payroll_backend.auth.domain.UserAccount;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PayrollUserDetails implements UserDetails {

    private final UserAccount userAccount;

    public PayrollUserDetails(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name())
        );
    }

    @Override
    public @Nullable String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getUsername();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return userAccount.isEnabled(); }

    public UUID getOrganizationId() {
        return userAccount.getOrganization().getId();
    }
}
