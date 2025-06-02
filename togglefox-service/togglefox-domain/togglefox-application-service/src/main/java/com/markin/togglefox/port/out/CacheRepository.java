package com.markin.togglefox.port.out;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.model.FlagEvaluationResult;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface CacheRepository {

    /**
     * Cache a feature flag
     */
    void put(FeatureFlagId id, FeatureFlag featureFlag, Duration ttl);

    /**
     * Get cached feature flag
     */
    Optional<FeatureFlag> get(FeatureFlagId id);

    /**
     * Remove from cache
     */
    void evict(FeatureFlagId id);

    /**
     * Clear all cache
     */
    void clear();
}
