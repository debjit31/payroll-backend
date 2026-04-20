package com.example.payroll_backend.organization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private OrganizationController organizationController;

    @Test
    void createOrganization_validOrganization_shouldReturnOrganization() {
        Organization org = new Organization();
        org.setId(UUID.randomUUID());
        org.setName("Acme");
        org.setCountry("US");
        org.setCurrency("USD");
        org.setPayrollFrequency(PayrollFrequency.MONTHLY);

        when(organizationService.createOrganization(org)).thenReturn(org);

        Organization result = organizationController.createOrganization(org);

        assertThat(result).isEqualTo(org);
        assertThat(result.getName()).isEqualTo("Acme");
    }

    @Test
    void createOrganization_invalidType_shouldThrowClassCastException() {
        assertThatThrownBy(() -> organizationController.createOrganization("not an org"))
                .isInstanceOf(ClassCastException.class);
    }
}
