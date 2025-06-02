package com.markin.togglefox.service;

import com.markin.togglefox.domain.event.DomainEvent;
import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.strategy.AttributeBasedStrategy;
import com.markin.togglefox.domain.strategy.PercentageRolloutStrategy;
import com.markin.togglefox.domain.strategy.RolloutStrategy;
import com.markin.togglefox.domain.strategy.UserTargetingStrategy;
import com.markin.togglefox.dto.command.CreateFlagCommand;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.port.in.CreateFlagUseCase;
import com.markin.togglefox.port.out.EventPublisher;
import com.markin.togglefox.port.out.FeatureFlagRepository;

import java.util.*;

public class CreateFlagService implements CreateFlagUseCase {

    private final FeatureFlagRepository repository;
    private final EventPublisher eventPublisher;

    public CreateFlagService(FeatureFlagRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public FeatureFlag createFlag(CreateFlagCommand command) {
        Environment environment = Environment.of(command.getEnvironment());

        // Check if flag already exists
        if (repository.existsByNameAndEnvironment(command.getName(), environment)) {
            throw new IllegalArgumentException(
                    "Feature flag already exists: " + command.getName() + " in " + command.getEnvironment()
            );
        }

        // Generate ID and create strategy
        FeatureFlagId flagId = FeatureFlagId.generate();
        RolloutStrategy strategy = createStrategy(flagId, command.getStrategyType(), command.getStrategyConfig());

        // Create the flag
        FeatureFlag flag = FeatureFlag.create(
                flagId,
                command.getName(),
                command.getDescription(),
                environment,
                strategy
        );

        // Save and publish events
        FeatureFlag savedFlag = repository.save(flag);
        publishDomainEvents(savedFlag);

        return savedFlag;
    }

    private RolloutStrategy createStrategy(FeatureFlagId flagId, String strategyType, Map<String, Object> config) {
        switch (strategyType.toUpperCase()) {
            case "PERCENTAGE":
                Integer percentage = (Integer) config.get("percentage");
                if (percentage == null) {
                    throw new IllegalArgumentException("Percentage strategy requires 'percentage' configuration");
                }
                return new PercentageRolloutStrategy(flagId, percentage);

            case "USER_TARGETING":
                @SuppressWarnings("unchecked")
                List<String> userList = (List<String>) config.get("users");
                if (userList == null) {
                    throw new IllegalArgumentException("User targeting strategy requires 'users' configuration");
                }
                return new UserTargetingStrategy(flagId, new HashSet<>(userList));

            case "ATTRIBUTE_BASED":
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> rules = (Map<String, List<Object>>) config.get("rules");
                if (rules == null) {
                    throw new IllegalArgumentException("Attribute-based strategy requires 'rules' configuration");
                }
                Map<String, Set<Object>> attributeRules = new HashMap<>();
                rules.forEach((key, values) -> attributeRules.put(key, new HashSet<>(values)));
                return new AttributeBasedStrategy(flagId, attributeRules);

            default:
                throw new IllegalArgumentException("Unknown strategy type: " + strategyType);
        }
    }

    private void publishDomainEvents(FeatureFlag flag) {
        List<DomainEvent> events = flag.getAndClearDomainEvents();
        events.forEach(eventPublisher::publish);
    }
}
