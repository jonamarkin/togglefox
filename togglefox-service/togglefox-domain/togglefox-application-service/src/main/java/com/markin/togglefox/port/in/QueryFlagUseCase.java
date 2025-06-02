package com.markin.togglefox.port.in;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;

import java.util.List;
import java.util.Optional;

public interface QueryFlagUseCase {
    /**
     * Get a specific feature flag by name and environment
     */
    Optional<FeatureFlag> getFlag(String flagName, Environment environment);

    /**
     * Get a specific feature flag by ID
     */
    Optional<FeatureFlag> getFlagById(FeatureFlagId flagId);

    /**
     * List all flags in an environment
     */
    List<FeatureFlag> getAllFlags(Environment environment);

    /**
     * Search flags by name pattern
     */
    List<FeatureFlag> searchFlags(String namePattern, Environment environment);

    /**
     * Get all enabled flags in an environment
     */
    List<FeatureFlag> getEnabledFlags(Environment environment);

    /**
     * Get flags by multiple IDs efficiently
     */
    List<FeatureFlag> getFlagsByIds(List<FeatureFlagId> flagIds);

    /**
     * Check if a flag exists without loading it
     */
    boolean flagExists(String flagName, Environment environment);

    /**
     * Get flags with a specific rollout strategy type
     */
    List<FeatureFlag> getFlagsByStrategyType(String strategyType, Environment environment);

    /**
     * Get flags created by a specific user
     */
    List<FeatureFlag> getFlagsByCreator(String createdBy, Environment environment);
}
