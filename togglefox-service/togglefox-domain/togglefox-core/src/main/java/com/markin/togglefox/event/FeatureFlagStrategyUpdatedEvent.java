package com.markin.togglefox.event;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeatureFlagStrategyUpdatedEvent extends AbstractDomainEvent{

    private final FeatureFlagId flagId;
    private final String flagName;
    private final Environment environment;
    private final String previousStrategyType;
    private final String newStrategyType;

    public FeatureFlagStrategyUpdatedEvent(FeatureFlagId flagId, String flagName, Environment environment,
                                           String previousStrategyType, String newStrategyType,
                                           String updatedBy) {
        super(flagId.getValue(), "FeatureFlag", updatedBy);
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

    public String getPreviousStrategyType() {
        return previousStrategyType;
    }

    public String getNewStrategyType() {
        return newStrategyType;
    }
}
