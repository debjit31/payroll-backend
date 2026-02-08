CREATE TABLE employees (
    id UUID PRIMARY KEY,
    employee_code VARCHAR(50) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    organization_id UUID NOT NULL,
    employment_type VARCHAR(30),
    joining_date DATE NOT NULL,
    exit_date DATE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    created_by VARCHAR(100),

    CONSTRAINT fk_employee_org
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT uq_employee_code_per_org
        UNIQUE (organization_id, employee_code)
);
