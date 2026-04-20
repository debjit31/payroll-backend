package com.example.payroll_backend.auth.service;

import com.example.payroll_backend.auth.domain.Role;
import com.example.payroll_backend.auth.domain.UserAccount;
import com.example.payroll_backend.auth.dto.UserRegistrationRequest;
import com.example.payroll_backend.auth.dto.UserRegistrationResponse;
import com.example.payroll_backend.auth.repo.UserRepository;
import com.example.payroll_backend.organization.Organization;
import com.example.payroll_backend.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Test
    void registerUser_withOrganization_shouldReturnSuccessResponse() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Organization org = new Organization();
        org.setId(orgId);
        org.setName("Acme Corp");

        UserRegistrationRequest request = new UserRegistrationRequest(
                "john_doe", "password123", Role.ORG_ADMIN, orgId, true);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        UserAccount savedUser = new UserAccount();
        savedUser.setId(userId);
        savedUser.setUsername("john_doe");
        savedUser.setPassword("encoded_password");
        savedUser.setRole(Role.ORG_ADMIN);
        savedUser.setOrganization(org);
        savedUser.setEnabled(true);
        when(userRepository.save(any(UserAccount.class))).thenReturn(savedUser);

        UserRegistrationResponse response = userRegistrationService.registerUser(request);

        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("john_doe");
        assertThat(response.getRole()).isEqualTo(Role.ORG_ADMIN);
        assertThat(response.getOrganizationId()).isEqualTo(orgId);
        assertThat(response.isEnabled()).isTrue();
        assertThat(response.getMessage()).isEqualTo("User registered successfully");

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserAccount.class));
    }

    @Test
    void registerUser_sysAdminWithoutOrganization_shouldReturnSuccessResponse() {
        UUID userId = UUID.randomUUID();

        UserRegistrationRequest request = new UserRegistrationRequest(
                "admin_user", "adminpass", Role.SYS_ADMIN, null, true);

        when(userRepository.findByUsername("admin_user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("adminpass")).thenReturn("encoded_adminpass");

        UserAccount savedUser = new UserAccount();
        savedUser.setId(userId);
        savedUser.setUsername("admin_user");
        savedUser.setPassword("encoded_adminpass");
        savedUser.setRole(Role.SYS_ADMIN);
        savedUser.setOrganization(null);
        savedUser.setEnabled(true);
        when(userRepository.save(any(UserAccount.class))).thenReturn(savedUser);

        UserRegistrationResponse response = userRegistrationService.registerUser(request);

        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("admin_user");
        assertThat(response.getRole()).isEqualTo(Role.SYS_ADMIN);
        assertThat(response.getOrganizationId()).isNull();
        assertThat(response.isEnabled()).isTrue();

        verify(organizationRepository, never()).findById(any());
    }

    @Test
    void registerUser_duplicateUsername_shouldThrowIllegalArgumentException() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "existing_user", "pass", Role.EMPLOYEE, UUID.randomUUID(), true);

        when(userRepository.findByUsername("existing_user"))
                .thenReturn(Optional.of(new UserAccount()));

        assertThatThrownBy(() -> userRegistrationService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists: existing_user");

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_invalidOrganizationId_shouldThrowIllegalArgumentException() {
        UUID badOrgId = UUID.randomUUID();

        UserRegistrationRequest request = new UserRegistrationRequest(
                "new_user", "pass", Role.HR_MANAGER, badOrgId, true);

        when(userRepository.findByUsername("new_user")).thenReturn(Optional.empty());
        when(organizationRepository.findById(badOrgId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRegistrationService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Organization not found with id: " + badOrgId);
    }

    @Test
    void registerUser_disabledAccount_shouldPersistEnabledFalse() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Organization org = new Organization();
        org.setId(orgId);

        UserRegistrationRequest request = new UserRegistrationRequest(
                "disabled_user", "pass", Role.EMPLOYEE, orgId, false);

        when(userRepository.findByUsername("disabled_user")).thenReturn(Optional.empty());
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        UserAccount savedUser = new UserAccount();
        savedUser.setId(userId);
        savedUser.setUsername("disabled_user");
        savedUser.setRole(Role.EMPLOYEE);
        savedUser.setOrganization(org);
        savedUser.setEnabled(false);
        when(userRepository.save(any(UserAccount.class))).thenReturn(savedUser);

        UserRegistrationResponse response = userRegistrationService.registerUser(request);

        assertThat(response.isEnabled()).isFalse();
    }
}

