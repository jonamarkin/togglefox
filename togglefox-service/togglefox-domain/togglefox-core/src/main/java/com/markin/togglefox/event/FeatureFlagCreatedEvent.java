package com.markin.togglefox.event;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeatureFlagCreatedEvent extends AbstractDomainEvent {
    private final FeatureFlagId flagId;
    private final String flagName;
    private final String description;
    private final Environment environment;

    public FeatureFlagCreatedEvent(FeatureFlagId flagId, String flagName, String description,
                                   Environment environment, String createdBy) {
        super(flagId.value(), "FeatureFlag", createdBy);
        this.flagId = flagId;
        this.flagName = flagName;
        this.description = description;
        this.environment = environment;
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

    public String getDescription() {
        return description;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
