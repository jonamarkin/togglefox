package com.markin.togglefox.port.out;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.model.FlagEvaluationResult;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface CacheRepository {

    /**
     * Cache a feature flag
     */
    void cacheFlag(FeatureFlag flag);

    /**
     * Cache a feature flag with custom TTL
     */
    void cacheFlag(FeatureFlag flag, Duration ttl);

    /**
     * Get a feature flag from cache
     */
    Optional<FeatureFlag> getFlag(String flagName, Environment environment);

    /**
     * Cache evaluation result for performance
     */
    void cacheEvaluationResult(String flagName, String userId, Environment environment,
                               FlagEvaluationResult result);

    /**
     * Get cached evaluation result
     */
    Optional<FlagEvaluationResult> getCachedEvaluationResult(String flagName, String userId,
                                                             Environment environment);

    /**
     * Evict a specific flag from cache
     */
    void evictFlag(String flagName, Environment environment);

    /**
     * Evict all evaluation results for a flag
     */
    void evictFlagEvaluations(String flagName, Environment environment);

    /**
     * Evict all flags for an environment
     */
    void evictEnvironment(Environment environment);

    /**
     * Get all cached flag names for an environment
     */
    List<String> getCachedFlagNames(Environment environment);

    /**
     * Check if flag exists in cache
     */
    boolean exists(String flagName, Environment environment);

    /**
     * Clear all cache entries
     */
    void clearAll();

    /**
     * Get cache statistics
     */
    CacheStats getStats();
}
