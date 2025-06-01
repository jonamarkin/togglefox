package com.markin.togglefox.event;

import com.markin.togglefox.valueobject.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeatureFlagStrategyUpdatedEvent implements DomainEvent{

    private final FeatureFlagId flagId;
    private final String flagName;
    private final String strategyType;
    private final LocalDateTime occurredOn;

    public FeatureFlagStrategyUpdatedEvent(FeatureFlagId flagId, String flagName, String strategyType) {
        this.flagId = Objects.requireNonNull(flagId);
        this.flagName = Objects.requireNonNull(flagName);
        this.strategyType = Objects.requireNonNull(strategyType);
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
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

    public String getStrategyType() {
        return strategyType;
    }
}
