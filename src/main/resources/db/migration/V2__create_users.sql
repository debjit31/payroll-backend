CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    organization_id UUID NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP,
    created_at TIMESTAMP,
    created_by VARCHAR(100),

    CONSTRAINT fk_user_org
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
);
