package com.markin.togglefox.port.out;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagRepository {

    /**
     * Save a feature flag
     */
    FeatureFlag save(FeatureFlag featureFlag);

    /**
     * Find by ID
     */
    Optional<FeatureFlag> findById(FeatureFlagId id);

    /**
     * Find by name and environment
     */
    Optional<FeatureFlag> findByNameAndEnvironment(String name, Environment environment);

    /**
     * Find all flags in an environment
     */
    List<FeatureFlag> findByEnvironment(Environment environment);

    /**
     * Delete a feature flag
     */
    void delete(FeatureFlagId id);

    /**
     * Check if flag exists
     */
    boolean existsById(FeatureFlagId id);

    /**
     * Check if flag name exists in environment
     */
    boolean existsByNameAndEnvironment(String name, Environment environment);

    /**
     * Get all feature flags
     */
    List<FeatureFlag> findAll();

}
