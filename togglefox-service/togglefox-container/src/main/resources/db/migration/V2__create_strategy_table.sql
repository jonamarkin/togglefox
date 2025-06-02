CREATE TABLE strategies
(
    id              VARCHAR(36) PRIMARY KEY,
    feature_flag_id VARCHAR(36) NOT NULL,
    strategy_type   VARCHAR(50) NOT NULL,
    configuration   TEXT,

    CONSTRAINT fk_strategies_feature_flag
        FOREIGN KEY (feature_flag_id)
            REFERENCES feature_flags (id)
            ON DELETE CASCADE
);

-- Create unique constraint to ensure one strategy per flag
CREATE UNIQUE INDEX uk_strategies_feature_flag_id ON strategies (feature_flag_id);