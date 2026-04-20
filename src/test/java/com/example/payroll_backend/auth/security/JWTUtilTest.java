package com.example.payroll_backend.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JWTUtilTest {

    private JWTUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // Generate a 256-bit key encoded as Base64
        String secret = Base64.getEncoder().encodeToString(
                "thisisaverystrongsecretkeythatshouldbechangedinproductionandkeptsecurelyinenvironmentvariables".getBytes());
        jwtUtil = new JWTUtil(secret);
        ReflectionTestUtils.setField(jwtUtil, "expirationTimeMs", 1800000L);
    }

    @Test
    void generateToken_shouldReturnNonEmptyString() {
        UserDetails userDetails = new User("testuser", "pass", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        UserDetails userDetails = new User("john_doe", "pass", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertThat(username).isEqualTo("john_doe");
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        UserDetails userDetails = new User("testuser", "pass", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        boolean valid = jwtUtil.isTokenValid(token, "testuser");
        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_withWrongUsername_shouldReturnFalse() {
        UserDetails userDetails = new User("testuser", "pass", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        boolean valid = jwtUtil.isTokenValid(token, "otheruser");
        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_shouldReturnFalse() {
        ReflectionTestUtils.setField(jwtUtil, "expirationTimeMs", -1000L);
        UserDetails userDetails = new User("testuser", "pass", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        // Expired tokens should throw or return false
        try {
            boolean valid = jwtUtil.isTokenValid(token, "testuser");
            assertThat(valid).isFalse();
        } catch (Exception e) {
            // ExpiredJwtException is also acceptable
            assertThat(e).isNotNull();
        }
    }

    @Test
    void extractUsername_withTamperedToken_shouldThrow() {
        UserDetails userDetails = new User("testuser", "pass", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        String tampered = token + "tampered";
        assertThatThrownBy(() -> jwtUtil.extractUsername(tampered)).isInstanceOf(Exception.class);
    }
}
