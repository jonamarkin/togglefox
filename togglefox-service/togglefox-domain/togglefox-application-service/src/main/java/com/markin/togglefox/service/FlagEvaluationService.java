package com.markin.togglefox.service;

import com.markin.togglefox.domain.exception.FlagNotFoundException;
import com.markin.togglefox.domain.model.*;
import com.markin.togglefox.dto.query.EvaluateFlagQuery;
import com.markin.togglefox.port.in.FlagEvaluationUseCase;
import com.markin.togglefox.port.out.CacheRepository;
import com.markin.togglefox.port.out.FeatureFlagRepository;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public class FlagEvaluationService implements FlagEvaluationUseCase {

    private final FeatureFlagRepository repository;
    private final CacheRepository cache;

    public FlagEvaluationService(FeatureFlagRepository repository, CacheRepository cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Override
    public FlagEvaluationResult evaluateFlag(EvaluateFlagQuery query) {
        Environment environment = Environment.of(query.getEnvironment());

        // Try cache first
        Optional<FeatureFlag> cachedFlag = findFlagInCache(query.getFlagName(), environment);
        FeatureFlag flag = cachedFlag.orElseGet(() -> findAndCacheFlag(query.getFlagName(), environment));

        if (flag == null) {
            return FlagEvaluationResult.disabled(
                    FeatureFlagId.of("unknown"),
                    "Feature flag not found: " + query.getFlagName()
            );
        }

        // Build evaluation context
        EvaluationContext context = query.getUserId() != null
                ? EvaluationContext.forUserWithAttributes(query.getUserId(), query.getAttributes())
                : EvaluationContext.anonymous();

        return flag.evaluate(context);
    }

    private Optional<FeatureFlag> findFlagInCache(String flagName, Environment environment) {
        return Optional.empty(); // Simplified for now

        /** TODO: Implement cache lookup logic
         * Example:
         * String cacheKey = flagName + ":" + environment.getName();
         * return cache.get(cacheKey, FeatureFlag.class);
         */
    }

    private FeatureFlag findAndCacheFlag(String flagName, Environment environment) {
        Optional<FeatureFlag> flag = repository.findByNameAndEnvironment(flagName, environment);

        if (flag.isPresent()) {
            // Cache for 5 minutes
            cache.put(flag.get().getId(), flag.get(), Duration.ofMinutes(5));
        }

        return flag.orElse(null);
    }
}
