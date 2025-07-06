-- Create tenant_data table for storing multi-tenant payloads
CREATE TABLE IF NOT EXISTS tenant_data (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    data_payload JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for tenant_id for better performance
CREATE INDEX IF NOT EXISTS idx_tenant_data_tenant_id ON tenant_data(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tenant_data_created_at ON tenant_data(created_at);

-- No local users table—authentication is managed by AWS Cognito

-- Verify the setup
SELECT 'Database setup completed successfully with Cognito integration!' as status;
SELECT 'tenant_data table ready for use.' as info;