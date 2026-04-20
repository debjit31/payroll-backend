package com.example.payroll_backend.auth.api;

import com.example.payroll_backend.auth.dto.AuthRequest;
import com.example.payroll_backend.auth.dto.AuthResponse;
import com.example.payroll_backend.auth.security.JWTUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void loginPage_validCredentials_shouldReturnJwt() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(userDetails)).thenReturn("test-jwt-token");

        ResponseEntity<?> response = authenticationController.loginPage(authRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);
        assertThat(((AuthResponse) response.getBody()).getJwt()).isEqualTo("test-jwt-token");
    }

    @Test
    void loginPage_invalidCredentials_shouldReturn401() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("bad");
        authRequest.setPassword("credentials");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authenticationController.loginPage(authRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo("Bad credentials");
    }

    @Test
    void getWelcome_shouldReturnWelcomeMessage() {
        String result = authenticationController.getWelcome();
        assertThat(result).isEqualTo("Welcome to the Payroll System!");
    }
}
