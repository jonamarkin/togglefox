package com.markin.togglefox.service;

import com.markin.togglefox.domain.event.DomainEvent;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.strategy.AttributeBasedStrategy;
import com.markin.togglefox.domain.strategy.PercentageRolloutStrategy;
import com.markin.togglefox.domain.strategy.RolloutStrategy;
import com.markin.togglefox.domain.strategy.UserTargetingStrategy;
import com.markin.togglefox.dto.command.EnableFlagCommand;
import com.markin.togglefox.dto.command.ToggleFlagCommand;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.dto.command.UpdateStrategyCommand;
import com.markin.togglefox.port.in.ManageFlagUseCase;
import com.markin.togglefox.port.out.CacheRepository;
import com.markin.togglefox.port.out.EventPublisher;
import com.markin.togglefox.port.out.FeatureFlagRepository;

import java.util.*;

public class ManageFlagService implements ManageFlagUseCase {

    private final FeatureFlagRepository repository;
    private final CacheRepository cache;
    private final EventPublisher eventPublisher;

    public ManageFlagService(FeatureFlagRepository repository, CacheRepository cache, EventPublisher eventPublisher) {
        this.repository = repository;
        this.cache = cache;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void enableFlag(EnableFlagCommand command) {
        FeatureFlagId flagId = FeatureFlagId.of(command.getFlagId());
        FeatureFlag flag = findFlagOrThrow(flagId);

        flag.enable();

        repository.save(flag);
        cache.evict(flagId); // Invalidate cache
        publishDomainEvents(flag);
    }

    @Override
    public void disableFlag(FeatureFlagId flagId) {
        FeatureFlag flag = findFlagOrThrow(flagId);

        flag.disable();

        repository.save(flag);
        cache.evict(flagId); // Invalidate cache
        publishDomainEvents(flag);
    }

    @Override
    public void updateStrategy(UpdateStrategyCommand command) {
        FeatureFlagId flagId = FeatureFlagId.of(command.getFlagId());
        FeatureFlag flag = findFlagOrThrow(flagId);

        RolloutStrategy newStrategy = createStrategy(flagId, command.getStrategyType(), command.getStrategyConfig());
        flag.updateStrategy(newStrategy);

        repository.save(flag);
        cache.evict(flagId); // Invalidate cache
        publishDomainEvents(flag);
    }

    @Override
    public Optional<FeatureFlag> getFlag(FeatureFlagId flagId) {
        return repository.findById(flagId);
    }

    private FeatureFlag findFlagOrThrow(FeatureFlagId flagId) {
        return repository.findById(flagId)
                .orElseThrow(() -> new IllegalArgumentException("Feature flag not found: " + flagId));
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
