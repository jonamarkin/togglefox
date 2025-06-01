package com.markin.togglefox.event;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeatureFlagCreatedEvent implements DomainEvent {
    private final FeatureFlagId flagId;
    private final String flagName;
    private final Environment environment;
    private final LocalDateTime occurredOn;

    public FeatureFlagCreatedEvent(FeatureFlagId flagId, String flagName, Environment environment) {
        this.flagId = Objects.requireNonNull(flagId);
        this.flagName = Objects.requireNonNull(flagName);
        this.environment = Objects.requireNonNull(environment);
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "FeatureFlagCreated";
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
}
