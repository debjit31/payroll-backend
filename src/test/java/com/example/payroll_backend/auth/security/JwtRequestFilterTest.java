package com.example.payroll_backend.auth.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private PayrollUserDetailsService payrollUserDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_noAuthorizationHeader_shouldContinueFilterChain() throws Exception {
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_authHeaderWithoutBearerPrefix_shouldContinueWithoutAuth() throws Exception {
        request.addHeader("Authorization", "Basic some-token");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_validBearerToken_shouldSetAuthentication() throws Exception {
        String jwt = "valid-token";
        request.addHeader("Authorization", "Bearer " + jwt);

        UserDetails userDetails = new User("testuser", "pass", Collections.emptyList());
        when(jwtUtil.extractUsername(jwt)).thenReturn("testuser");
        when(payrollUserDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(jwt, "testuser")).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("testuser");
    }

    @Test
    void doFilter_invalidToken_shouldNotSetAuthentication() throws Exception {
        String jwt = "invalid-token";
        request.addHeader("Authorization", "Bearer " + jwt);

        UserDetails userDetails = new User("testuser", "pass", Collections.emptyList());
        when(jwtUtil.extractUsername(jwt)).thenReturn("testuser");
        when(payrollUserDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(jwt, "testuser")).thenReturn(false);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_jwtException_shouldReturn401() throws Exception {
        request.addHeader("Authorization", "Bearer bad-token");
        when(jwtUtil.extractUsername("bad-token")).thenThrow(new JwtException("Invalid JWT"));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains("JWT error");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilter_unexpectedException_shouldReturn500() throws Exception {
        request.addHeader("Authorization", "Bearer some-token");
        when(jwtUtil.extractUsername("some-token")).thenThrow(new RuntimeException("Unexpected"));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertThat(response.getContentAsString()).contains("Error");
        verify(filterChain, never()).doFilter(any(), any());
    }
}
