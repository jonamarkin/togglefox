package com.markin.togglefox.service;

import com.markin.togglefox.dto.command.ToggleFlagCommand;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.port.in.ManageFlagUseCase;
import com.markin.togglefox.port.out.CacheRepository;
import com.markin.togglefox.port.out.EventPublisher;
import com.markin.togglefox.port.out.FeatureFlagRepository;

import java.util.Objects;

public class ManageFlagService implements ManageFlagUseCase {

    private final FeatureFlagRepository repository;
    private final CacheRepository cacheRepository;
    private final EventPublisher eventPublisher;

    public ManageFlagService(FeatureFlagRepository repository,
                             CacheRepository cacheRepository,
                             EventPublisher eventPublisher) {
        this.repository = Objects.requireNonNull(repository);
        this.cacheRepository = Objects.requireNonNull(cacheRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void enableFlag(ToggleFlagCommand command) {
        FeatureFlag flag = repository.findById(command.flagId())
                .orElseThrow(() -> new IllegalArgumentException("Feature flag not found: " + command.flagId()));

        flag.enable();

        // Update the flag in the repository
        FeatureFlag updatedFlag = repository.save(flag);

        // Clear the flag from cache
        cacheRepository.evictFlag(updatedFlag.getName(), updatedFlag.getEnvironment());

        // Publish events related to the flag state change
        updatedFlag.getDomainEvents().forEach(eventPublisher::publish);
        // Clear domain events after publishing
        updatedFlag.clearDomainEvents();
    }

    @Override
    public void disableFlag(ToggleFlagCommand command) {
        FeatureFlag flag = repository.findById(command.flagId())
                .orElseThrow(() -> new IllegalArgumentException("Feature flag not found: " + command.flagId()));

        flag.disable();

        // Update the flag in the repository
        FeatureFlag updatedFlag = repository.save(flag);

        // Invalidate the flag in cache
        cacheRepository.evictFlag(updatedFlag.getName(), updatedFlag.getEnvironment());

        // Publish events related to the flag state change
        updatedFlag.getDomainEvents().forEach(eventPublisher::publish);
        // Clear domain events after publishing
        updatedFlag.clearDomainEvents();
    }
}
