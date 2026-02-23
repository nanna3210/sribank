CREATE TABLE IF NOT EXISTS login_attempt (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(255),
    attempt_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_login_attempt_username_time ON login_attempt (username, attempt_time);
CREATE INDEX idx_login_attempt_success_time ON login_attempt (success, attempt_time);
