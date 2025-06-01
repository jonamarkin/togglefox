package com.markin.togglefox.service;

import com.markin.togglefox.exception.FlagNotFoundException;
import com.markin.togglefox.model.EvaluationContext;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.model.FlagEvaluationResult;
import com.markin.togglefox.port.in.FlagEvaluationUseCase;
import com.markin.togglefox.port.out.CacheRepository;
import com.markin.togglefox.port.out.FeatureFlagRepository;

import java.util.Objects;
import java.util.Optional;

public class FlagEvaluationService implements FlagEvaluationUseCase {

    private final FeatureFlagRepository repository;
    private final CacheRepository cacheRepository;

    public FlagEvaluationService(FeatureFlagRepository repository, CacheRepository cacheRepository) {
        this.repository = Objects.requireNonNull(repository);
        this.cacheRepository = Objects.requireNonNull(cacheRepository);
    }



    @Override
    public FlagEvaluationResult evaluateFlag(String flagName, EvaluationContext context) {
        //Check evaluation cache first
        Optional<FlagEvaluationResult> cachedResult = cacheRepository.getCachedEvaluationResult(flagName, context.getUserId(), context.getEnvironment());

        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }

        //Check flag cache
        Optional<FeatureFlag> cachedFlag = cacheRepository.getFlag(flagName, context.getEnvironment());

        FeatureFlag featureFlag;
        if (cachedFlag.isPresent()) {
            featureFlag = cachedFlag.get();
        } else {
            // If not in cache, fetch from repository
            featureFlag = repository.findByNameAndEnvironment(flagName, context.getEnvironment())
                    .orElseThrow(() -> new FlagNotFoundException("Feature flag not found: " + flagName));
            // Cache the fetched flag
            cacheRepository.cacheFlag(featureFlag);
        }

        // Evaluate the flag using its strategies
        FlagEvaluationResult evaluationResult = featureFlag.evaluate(context);
        // Cache the evaluation result
        cacheRepository.cacheEvaluationResult(flagName, context.getUserId(), context.getEnvironment(), evaluationResult);

        return evaluationResult;
    }
}
