package com.example.payroll_backend.auth.service;

import com.example.payroll_backend.auth.domain.UserAccount;
import com.example.payroll_backend.auth.dto.UserRegistrationRequest;
import com.example.payroll_backend.auth.dto.UserRegistrationResponse;
import com.example.payroll_backend.auth.repo.UserRepository;
import com.example.payroll_backend.organization.Organization;
import com.example.payroll_backend.organization.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        // Validate username is not already taken
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // Validate organization exists (not required for SYS_ADMIN)
        Organization organization = null;
        if (request.getOrganizationId() != null) {
            organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Organization not found with id: " + request.getOrganizationId()));
        }

        // Create new user account
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(request.getUsername());
        userAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        userAccount.setRole(request.getRole());
        userAccount.setOrganization(organization);
        userAccount.setEnabled(request.isEnabled());

        // Save user
        UserAccount savedUser = userRepository.save(userAccount);
        log.info("User registered successfully: {} with id: {}", savedUser.getUsername(), savedUser.getId());

        // Create response
        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setRole(savedUser.getRole());
        response.setOrganizationId(savedUser.getOrganization() != null ? savedUser.getOrganization().getId() : null);
        response.setEnabled(savedUser.isEnabled());
        response.setMessage("User registered successfully");

        return response;
    }
}
