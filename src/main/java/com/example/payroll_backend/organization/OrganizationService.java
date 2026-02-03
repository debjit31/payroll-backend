package com.example.payroll_backend.organization;

import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    private OrganizationRepository organizationRepository;


    public Organization createOrganization(Organization org) {
        return organizationRepository.save(org);
    }
}
