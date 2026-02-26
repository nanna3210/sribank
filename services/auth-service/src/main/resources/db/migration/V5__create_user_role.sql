CREATE TABLE IF NOT EXISTS user_role (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES auth_user (id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES auth_role (id)
);

INSERT INTO user_role (user_id, role_id, assigned_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM auth_user u
JOIN auth_role r ON r.code = 'USER'
WHERE NOT EXISTS (
    SELECT 1
    FROM user_role ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
);
