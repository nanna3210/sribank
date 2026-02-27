ALTER TABLE refresh_token
    ADD COLUMN family_id VARCHAR(36) NULL,
    ADD COLUMN parent_token_id VARCHAR(36) NULL;

UPDATE refresh_token
SET family_id = id
WHERE family_id IS NULL;

ALTER TABLE refresh_token
    MODIFY COLUMN family_id VARCHAR(36) NOT NULL;

CREATE INDEX idx_refresh_token_family_id ON refresh_token (family_id);
