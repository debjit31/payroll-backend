-- Make organization_id nullable to support SYS_ADMIN users
ALTER TABLE users
ALTER COLUMN organization_id DROP NOT NULL;
