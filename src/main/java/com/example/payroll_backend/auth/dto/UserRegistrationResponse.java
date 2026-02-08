package com.example.payroll_backend.auth.dto;

import com.example.payroll_backend.auth.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponse {

    private UUID id;
    private String username;
    private Role role;
    private UUID organizationId;
    private boolean enabled;
    private String message;
}
