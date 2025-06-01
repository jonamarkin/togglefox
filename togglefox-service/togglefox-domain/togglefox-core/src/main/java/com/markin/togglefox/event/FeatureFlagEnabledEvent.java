package com.markin.togglefox.event;

import com.markin.togglefox.valueobject.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeatureFlagEnabledEvent implements DomainEvent {
    private final FeatureFlagId flagId;
    private final String flagName;
    private final LocalDateTime occurredOn;

    public FeatureFlagEnabledEvent(FeatureFlagId flagId, String flagName) {
        this.flagId = Objects.requireNonNull(flagId);
        this.flagName = Objects.requireNonNull(flagName);
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "FeatureFlagEnabled";
    }

    public FeatureFlagId getFlagId() {
        return flagId;
    }

    public String getFlagName() {
        return flagName;
    }
}
