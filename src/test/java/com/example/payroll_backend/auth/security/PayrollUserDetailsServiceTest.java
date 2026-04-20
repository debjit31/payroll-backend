package com.example.payroll_backend.auth.security;

import com.example.payroll_backend.auth.domain.Role;
import com.example.payroll_backend.auth.domain.UserAccount;
import com.example.payroll_backend.auth.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PayrollUserDetailsService payrollUserDetailsService;

    @Test
    void loadUserByUsername_userExists_shouldReturnPayrollUserDetails() {
        UserAccount userAccount = new UserAccount();
        userAccount.setId(UUID.randomUUID());
        userAccount.setUsername("john");
        userAccount.setPassword("encoded");
        userAccount.setRole(Role.EMPLOYEE);
        userAccount.setEnabled(true);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(userAccount));

        UserDetails result = payrollUserDetailsService.loadUserByUsername("john");

        assertThat(result).isInstanceOf(PayrollUserDetails.class);
        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getPassword()).isEqualTo("encoded");
    }

    @Test
    void loadUserByUsername_userNotFound_shouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> payrollUserDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: unknown");
    }
}
