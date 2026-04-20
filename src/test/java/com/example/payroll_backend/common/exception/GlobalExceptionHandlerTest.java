package com.example.payroll_backend.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handle_runtimeException_shouldReturn400WithMessage() {
        RuntimeException ex = new RuntimeException("Something went wrong");
        ResponseEntity<?> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Something went wrong");
    }

    @Test
    void handle_runtimeExceptionWithNullMessage_shouldReturn400WithNullBody() {
        RuntimeException ex = new RuntimeException((String) null);
        ResponseEntity<?> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }
}
