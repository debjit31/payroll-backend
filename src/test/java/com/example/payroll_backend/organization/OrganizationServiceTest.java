package com.example.payroll_backend.organization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @Test
    void createOrganization_shouldDelegateToRepositoryAndReturnSaved() {
        Organization org = new Organization();
        org.setName("Test Corp");
        org.setCountry("US");
        org.setCurrency("USD");
        org.setPayrollFrequency(PayrollFrequency.MONTHLY);

        Organization saved = new Organization();
        saved.setId(UUID.randomUUID());
        saved.setName("Test Corp");
        saved.setCountry("US");
        saved.setCurrency("USD");
        saved.setPayrollFrequency(PayrollFrequency.MONTHLY);

        when(organizationRepository.save(org)).thenReturn(saved);

        Organization result = organizationService.createOrganization(org);

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getName()).isEqualTo("Test Corp");
        verify(organizationRepository).save(org);
    }
}
