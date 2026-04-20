package com.example.payroll_backend.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationEntryPointTest {

    private final JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();

    @Test
    void commence_shouldSetUnauthorizedStatusAndWriteMessage() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        entryPoint.commence(request, response, exception);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).isEqualTo("Unauthorized: Bad credentials");
    }
}
