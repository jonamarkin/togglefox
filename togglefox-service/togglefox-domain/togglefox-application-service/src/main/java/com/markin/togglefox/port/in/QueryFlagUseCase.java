package com.markin.togglefox.port.in;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.model.FeatureFlagId;

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
}
