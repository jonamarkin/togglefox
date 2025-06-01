package com.markin.togglefox.event;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeatureFlagEnabledEvent extends AbstractDomainEvent {
    private final FeatureFlagId flagId;
    private final String flagName;
    private final Environment environment;
    private final String reason;

    public FeatureFlagEnabledEvent(FeatureFlagId flagId, String flagName, Environment environment,
                                   String enabledBy, String reason) {
        super(flagId.value(), "FeatureFlag", enabledBy);
        this.flagId = flagId;
        this.flagName = flagName;
        this.environment = environment;
        this.reason = reason;
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

    public Environment getEnvironment() {
        return environment;
    }

    public String getReason() {
        return reason;
    }
}
