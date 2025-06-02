-- Index for flag evaluation queries (most common operation)
CREATE INDEX idx_feature_flags_name_environment_enabled
    ON feature_flags (name, environment, enabled);

-- Index for strategy lookups
CREATE INDEX idx_strategies_type ON strategies (strategy_type);

-- Add comments for documentation
COMMENT
ON TABLE feature_flags IS 'Feature flags with basic metadata and state';
COMMENT
ON TABLE strategies IS 'Rollout strategies for feature flags';

COMMENT
ON COLUMN feature_flags.id IS 'Unique identifier for the feature flag';
COMMENT
ON COLUMN feature_flags.name IS 'Human-readable name for the feature flag';
COMMENT
ON COLUMN feature_flags.environment IS 'Target environment (dev, staging, prod, etc.)';
COMMENT
ON COLUMN feature_flags.enabled IS 'Whether the flag is currently enabled';
COMMENT
ON COLUMN feature_flags.version IS 'Optimistic locking version';

COMMENT
ON COLUMN strategies.strategy_type IS 'Type of rollout strategy (PERCENTAGE, USER_TARGETING, etc.)';
COMMENT
ON COLUMN strategies.configuration IS 'JSON configuration for the strategy';