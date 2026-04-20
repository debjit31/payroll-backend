package com.example.payroll_backend.auth.security;

import com.example.payroll_backend.auth.domain.Role;
import com.example.payroll_backend.auth.domain.UserAccount;
import com.example.payroll_backend.organization.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayrollUserDetailsTest {

    private UserAccount createUserAccount(Role role, boolean enabled, Organization org) {
        UserAccount ua = new UserAccount();
        ua.setId(UUID.randomUUID());
        ua.setUsername("testuser");
        ua.setPassword("hashed_pw");
        ua.setRole(role);
        ua.setEnabled(enabled);
        ua.setOrganization(org);
        return ua;
    }

    @Test
    void getAuthorities_shouldReturnRolePrefixed() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.HR_MANAGER, true, null));
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_HR_MANAGER");
    }

    @Test
    void getAuthorities_allRoles() {
        for (Role role : Role.values()) {
            PayrollUserDetails details = new PayrollUserDetails(createUserAccount(role, true, null));
            assertThat(details.getAuthorities().iterator().next().getAuthority())
                    .isEqualTo("ROLE_" + role.name());
        }
    }

    @Test
    void getPassword_shouldReturnAccountPassword() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.EMPLOYEE, true, null));
        assertThat(details.getPassword()).isEqualTo("hashed_pw");
    }

    @Test
    void getUsername_shouldReturnAccountUsername() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.EMPLOYEE, true, null));
        assertThat(details.getUsername()).isEqualTo("testuser");
    }

    @Test
    void isEnabled_enabledAccount_shouldReturnTrue() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.EMPLOYEE, true, null));
        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    void isEnabled_disabledAccount_shouldReturnFalse() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.EMPLOYEE, false, null));
        assertThat(details.isEnabled()).isFalse();
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.EMPLOYEE, true, null));
        assertThat(details.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.EMPLOYEE, true, null));
        assertThat(details.isAccountNonLocked()).isTrue();
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.EMPLOYEE, true, null));
        assertThat(details.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void getOrganizationId_withOrganization_shouldReturnId() {
        Organization org = new Organization();
        UUID orgId = UUID.randomUUID();
        org.setId(orgId);
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.EMPLOYEE, true, org));
        assertThat(details.getOrganizationId()).isEqualTo(orgId);
    }

    @Test
    void getOrganizationId_withNullOrganization_shouldThrowNPE() {
        PayrollUserDetails details = new PayrollUserDetails(createUserAccount(Role.SYS_ADMIN, true, null));
        assertThatThrownBy(details::getOrganizationId).isInstanceOf(NullPointerException.class);
    }
}
