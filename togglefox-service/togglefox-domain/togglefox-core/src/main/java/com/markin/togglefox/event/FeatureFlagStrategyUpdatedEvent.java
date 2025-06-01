package com.markin.togglefox.event;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlagId;
import com.markin.togglefox.strategy.RolloutStrategy;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeatureFlagStrategyUpdatedEvent extends AbstractDomainEvent{

    private final FeatureFlagId flagId;
    private final String flagName;
    private final Environment environment;
    private final RolloutStrategy previousStrategyType;
    private final RolloutStrategy newStrategyType;

    public FeatureFlagStrategyUpdatedEvent(FeatureFlagId flagId, String flagName, Environment environment,
                                           RolloutStrategy previousStrategyType, RolloutStrategy newStrategyType,
                                           String updatedBy) {
        super(flagId.value(), "FeatureFlag", updatedBy);
        this.flagId = flagId;
        this.flagName = flagName;
        this.environment = environment;
        this.previousStrategyType = previousStrategyType;
        this.newStrategyType = newStrategyType;
    }

    @Override
    public String getEventType() {
        return "FeatureFlagStrategyUpdated";
    }

    public FeatureFlagId getFlagId() {
        return flagId;
    }

    public String getFlagName() {
        return flagName;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public RolloutStrategy getPreviousStrategyType() {
        return previousStrategyType;
    }

    public RolloutStrategy getNewStrategyType() {
        return newStrategyType;
    }
}
