package com.markin.togglefox.service;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.model.FeatureFlagId;
import com.markin.togglefox.port.in.QueryFlagUseCase;
import com.markin.togglefox.port.out.CacheRepository;
import com.markin.togglefox.port.out.FeatureFlagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class QueryFlagService implements QueryFlagUseCase {

    private static final Logger logger = LoggerFactory.getLogger(QueryFlagService.class);

    private final FeatureFlagRepository repository;
    private final CacheRepository cacheRepository;

    public QueryFlagService(FeatureFlagRepository repository, CacheRepository cacheRepository) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.cacheRepository = Objects.requireNonNull(cacheRepository, "Cache repository cannot be null");
    }

    @Override
    public Optional<FeatureFlag> getFlag(String flagName, Environment environment) {
        return Optional.empty();
    }

    @Override
    public Optional<FeatureFlag> getFlagById(FeatureFlagId flagId) {
        return Optional.empty();
    }

    @Override
    public List<FeatureFlag> getAllFlags(Environment environment) {
        return List.of();
    }

    @Override
    public List<FeatureFlag> searchFlags(String namePattern, Environment environment) {
        return List.of();
    }
}
