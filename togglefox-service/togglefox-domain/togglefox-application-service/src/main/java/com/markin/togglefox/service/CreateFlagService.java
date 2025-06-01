package com.markin.togglefox.service;

import com.markin.togglefox.dto.command.CreateFlagCommand;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.model.FeatureFlagId;
import com.markin.togglefox.port.in.CreateFlagUseCase;
import com.markin.togglefox.port.out.EventPublisher;
import com.markin.togglefox.port.out.FeatureFlagRepository;

import java.util.Objects;

public class CreateFlagService implements CreateFlagUseCase {

    private final FeatureFlagRepository repository;
    private final EventPublisher eventPublisher;

    public CreateFlagService(FeatureFlagRepository repository, EventPublisher eventPublisher) {
        this.repository = Objects.requireNonNull(repository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public FeatureFlagId createFlag(CreateFlagCommand command) {
        //Check if the flag already exists
        if (repository.existsByNameAndEnvironment(command.name(), command.environment())) {
            throw new IllegalArgumentException("Feature flag with name '" + command.name() + "' already exists in environment '" + command.environment() + "'");
        }

        // Create the feature flag
        FeatureFlag flag = FeatureFlag.create(
                command.name(),
                command.description(),
                command.environment(),
                command.createdBy()
        );

        // Save the flag to the repository
        FeatureFlag savedFlag = repository.save(flag);

        // Publish an event for the created flag
        savedFlag.getDomainEvents().forEach(eventPublisher::publish);
        // Clear domain events after publishing
        savedFlag.clearDomainEvents();

        // Return the ID of the created flag
        return savedFlag.getId();
    }
}
