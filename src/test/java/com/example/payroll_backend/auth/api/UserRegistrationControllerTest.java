package com.example.payroll_backend.auth.api;
import com.example.payroll_backend.auth.domain.Role;
import com.example.payroll_backend.auth.dto.UserRegistrationRequest;
import com.example.payroll_backend.auth.dto.UserRegistrationResponse;
import com.example.payroll_backend.auth.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserRegistrationControllerTest {
    @Mock
    private UserRegistrationService userRegistrationService;
    @InjectMocks
    private UserRegistrationController userRegistrationController;
    @Test
    void registerUser_validRequest_shouldReturn201() {
        UUID orgId = UUID.randomUUID();
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", Role.ORG_ADMIN, orgId, true);
        UserRegistrationResponse expected = new UserRegistrationResponse(UUID.randomUUID(), "testuser", Role.ORG_ADMIN, orgId, true, "User registered successfully");
        when(userRegistrationService.registerUser(any())).thenReturn(expected);
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    @Test
    void registerUser_nullUsername_shouldReturn400() {
        UserRegistrationRequest request = new UserRegistrationRequest(null, "password", Role.EMPLOYEE, UUID.randomUUID(), true);
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Username is required");
    }
    @Test
    void registerUser_emptyUsername_shouldReturn400() {
        UserRegistrationRequest request = new UserRegistrationRequest("  ", "password", Role.EMPLOYEE, UUID.randomUUID(), true);
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Username is required");
    }
    @Test
    void registerUser_nullPassword_shouldReturn400() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", null, Role.EMPLOYEE, UUID.randomUUID(), true);
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Password is required");
    }
    @Test
    void registerUser_emptyPassword_shouldReturn400() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "  ", Role.EMPLOYEE, UUID.randomUUID(), true);
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Password is required");
    }
    @Test
    void registerUser_nullRole_shouldReturn400() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", null, UUID.randomUUID(), true);
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Role is required");
    }
    @Test
    void registerUser_nonSysAdminWithoutOrgId_shouldReturn400() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", Role.EMPLOYEE, null, true);
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Organization ID is required");
    }
    @Test
    void registerUser_sysAdminWithoutOrgId_shouldReturn201() {
        UserRegistrationRequest request = new UserRegistrationRequest("admin", "password", Role.SYS_ADMIN, null, true);
        UserRegistrationResponse expected = new UserRegistrationResponse(UUID.randomUUID(), "admin", Role.SYS_ADMIN, null, true, "OK");
        when(userRegistrationService.registerUser(any())).thenReturn(expected);
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
    @Test
    void registerUser_illegalArgument_shouldReturn400() {
        UserRegistrationRequest request = new UserRegistrationRequest("dup", "password", Role.SYS_ADMIN, null, true);
        when(userRegistrationService.registerUser(any())).thenThrow(new IllegalArgumentException("Username already exists: dup"));
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Username already exists: dup");
    }
    @Test
    void registerUser_unexpectedException_shouldReturn500() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", Role.SYS_ADMIN, null, true);
        when(userRegistrationService.registerUser(any())).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<?> response = userRegistrationController.registerUser(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().toString()).contains("DB error");
    }
}
