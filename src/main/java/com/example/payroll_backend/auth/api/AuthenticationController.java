package com.example.payroll_backend.auth.api;

import com.example.payroll_backend.auth.dto.AuthRequest;
import com.example.payroll_backend.auth.dto.AuthResponse;
import com.example.payroll_backend.auth.security.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user authentication and JWT token generation")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Operation(summary = "User login", description = "Authenticate user and generate JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginPage(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            // If authentication is successful, generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            log.info("Logged in user : {}", userDetails.getUsername());
            log.info("JWT Token genenerated : {}", jwt);

            // Return the JWT in the response
            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @Operation(summary = "Welcome message", description = "Public endpoint to verify API is running")
    @ApiResponse(responseCode = "200", description = "Welcome message returned")
    @GetMapping("/welcome")
    public String getWelcome() {
        return "Welcome to the Payroll System!";
    }
}
