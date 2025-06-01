package com.markin.togglefox.port.out;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.model.FeatureFlagId;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagRepository {

    /**
     * Save a feature flag (create or update)
     */
    FeatureFlag save(FeatureFlag featureFlag);

    /**
     * Find feature flag by ID
     */
    Optional<FeatureFlag> findById(FeatureFlagId flagId);

    /**
     * Delete feature flag by ID
     */
    void deleteById(FeatureFlagId flagId);

    /**
     * Check if feature flag exists by ID
     */
    boolean existsById(FeatureFlagId flagId);


    /**
     * Find feature flag by name and environment
     */
    Optional<FeatureFlag> findByNameAndEnvironment(String name, Environment environment);

    /**
     * Check if feature flag exists by name and environment
     */
    boolean existsByNameAndEnvironment(String name, Environment environment);

    /**
     * Find all feature flags in an environment
     */
    List<FeatureFlag> findByEnvironment(Environment environment);

    /**
     * Find all enabled feature flags in an environment
     */
    List<FeatureFlag> findByEnvironmentAndEnabledTrue(Environment environment);

    /**
     * Find feature flags by name pattern in an environment
     */
    List<FeatureFlag> findByNamePatternAndEnvironment(String namePattern, Environment environment);

    /**
     * Find feature flags by multiple IDs
     */
    List<FeatureFlag> findByIdIn(List<FeatureFlagId> flagIds);

    /**
     * Find feature flags by name list and environment
     */
    List<FeatureFlag> findByNameInAndEnvironment(List<String> names, Environment environment);

    /**
     * Find feature flags by strategy type and environment
     */
    List<FeatureFlag> findByStrategyTypeAndEnvironment(String strategyType, Environment environment);

    /**
     * Find feature flags by creator and environment
     */
    List<FeatureFlag> findByCreatedByAndEnvironment(String createdBy, Environment environment);


    /**
     * Count total flags in environment
     */
    long countByEnvironment(Environment environment);

    /**
     * Count enabled flags in environment
     */
    long countByEnvironmentAndEnabledTrue(Environment environment);

    /**
     * Count flags by strategy type
     */
    long countByStrategyTypeAndEnvironment(String strategyType, Environment environment);


    /**
     * Save multiple feature flags
     */
    List<FeatureFlag> saveAll(List<FeatureFlag> featureFlags);

    /**
     * Delete multiple feature flags by IDs
     */
    void deleteAllById(List<FeatureFlagId> flagIds);

    /**
     * Find all feature flags (use with caution)
     */
    List<FeatureFlag> findAll();

}
