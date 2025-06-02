package com.markin.togglefox.dataaccess.repository;

import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.port.out.CacheRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class CacheRepositoryImpl implements CacheRepository {

    private static final String KEY_PREFIX = "feature_flag:";

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(FeatureFlagId id, FeatureFlag featureFlag, Duration ttl) {
        String key = buildKey(id);
        redisTemplate.opsForValue().set(key, featureFlag, ttl);
    }

    @Override
    public Optional<FeatureFlag> get(FeatureFlagId id) {
        String key = buildKey(id);
        FeatureFlag cached = (FeatureFlag) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(cached);
    }

    @Override
    public void evict(FeatureFlagId id) {
        String key = buildKey(id);
        redisTemplate.delete(key);
    }

    @Override
    public void clear() {
        String pattern = KEY_PREFIX + "*";
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private String buildKey(FeatureFlagId id) {
        return KEY_PREFIX + id.getValue();
    }
}
