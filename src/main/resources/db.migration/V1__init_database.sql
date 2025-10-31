CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Main user table
CREATE TABLE if NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    verification_code VARCHAR(255),
    verification_expiration TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRET_TIMESTAMP
);


-- Main products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    name VARCHAR(150) NOT NULL,
    sku VARCHAR(80) NOT NULL,
    category VARCHAR(60) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    description VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_products_tenant_sku UNIQUE (tenant_id, sku)
);

-- Element collection table for features (Map<String, String>)
CREATE TABLE product_features (
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    feature_key VARCHAR(255) NOT NULL,
    features VARCHAR(255),
    PRIMARY KEY (product_id, feature_key)
);

-- Indexes
CREATE INDEX ix_products_tenant_category ON products (tenant_id, category);
CREATE INDEX ix_products_tenant_name ON products (tenant_id, name);
CREATE INDEX ix_products_tenant_sku ON products (tenant_id, sku);
