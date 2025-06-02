CREATE TABLE feature_flags
(
    id          VARCHAR(36) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    enabled     BOOLEAN      NOT NULL DEFAULT FALSE,
    environment VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version     BIGINT                DEFAULT 0,

    CONSTRAINT uk_feature_flags_name_environment UNIQUE (name, environment)
);

-- Create index for common queries
CREATE INDEX idx_feature_flags_environment ON feature_flags (environment);
CREATE INDEX idx_feature_flags_enabled ON feature_flags (enabled);