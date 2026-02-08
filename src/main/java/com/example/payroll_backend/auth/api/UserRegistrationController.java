package com.example.payroll_backend.auth.api;

import com.example.payroll_backend.auth.domain.Role;
import com.example.payroll_backend.auth.dto.UserRegistrationRequest;
import com.example.payroll_backend.auth.dto.UserRegistrationResponse;
import com.example.payroll_backend.auth.service.UserRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "User Registration", description = "API for user registration and account management")
public class UserRegistrationController {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Operation(summary = "Register a new user", description = "Creates a new user account with encrypted password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = UserRegistrationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or username already exists", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            log.info("Registration request received for username: {}", request.getUsername());

            // Validate request
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }

            if (request.getRole() == null) {
                return ResponseEntity.badRequest().body("Role is required");
            }

            // SYS_ADMIN doesn't need an organization, but other roles do
            if (request.getRole() != Role.SYS_ADMIN && request.getOrganizationId() == null) {
                return ResponseEntity.badRequest().body("Organization ID is required");
            }

            // Register user
            UserRegistrationResponse response = userRegistrationService.registerUser(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during registration: " + e.getMessage());
        }
    }
}
