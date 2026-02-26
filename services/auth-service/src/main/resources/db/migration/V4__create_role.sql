CREATE TABLE IF NOT EXISTS auth_role (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_auth_role_code UNIQUE (code)
);

INSERT INTO auth_role (id, code, name)
SELECT 'b6fd3409-9702-4ba8-8bd4-a5e3f38fb9db', 'USER', 'Standard User'
WHERE NOT EXISTS (SELECT 1 FROM auth_role WHERE code = 'USER');

INSERT INTO auth_role (id, code, name)
SELECT '7f9447d3-4cc3-402d-b507-1edfd6d70117', 'ADMIN', 'Administrator'
WHERE NOT EXISTS (SELECT 1 FROM auth_role WHERE code = 'ADMIN');
