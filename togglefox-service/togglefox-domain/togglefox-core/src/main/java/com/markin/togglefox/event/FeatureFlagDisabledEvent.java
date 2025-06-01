package com.markin.togglefox.event;

import com.markin.togglefox.model.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeatureFlagDisabledEvent implements DomainEvent {
    private final FeatureFlagId flagId;
    private final String flagName;
    private final LocalDateTime occurredOn;

    public FeatureFlagDisabledEvent(FeatureFlagId flagId, String flagName) {
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
        return "FeatureFlagDisabled";
    }

    public FeatureFlagId getFlagId() {
        return flagId;
    }

    public String getFlagName() {
        return flagName;
    }
}
