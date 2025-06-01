package com.markin.togglefox.port.out;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlag;

import java.time.Duration;
import java.util.Optional;

public interface CachePort {

    void cacheFlag(String key, FeatureFlag flag, Duration ttl);
    Optional<FeatureFlag> getCachedFlag(String key);
    void invalidateFlag(String flagName, Environment environment);

    default String generateCacheKey(String flagName, Environment environment) {
        return String.format("flag:%s:%s", flagName, environment.getName());
    }
}
