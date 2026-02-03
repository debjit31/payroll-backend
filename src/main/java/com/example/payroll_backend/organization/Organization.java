package com.example.payroll_backend.organization;

import com.example.payroll_backend.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String country;
    private String currency;

//    @Enumerated(EnumType.STRING)
//    private PayrollFrequency payrollFrequency;
}

