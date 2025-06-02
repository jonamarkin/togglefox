package com.markin.togglefox.domain.event;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlagId;

public class FeatureFlagEvaluatedEvent extends AbstractDomainEvent{
    private final FeatureFlagId flagId;
    private final String flagName;
    private final Environment environment;
    private final String userId;
    private final boolean result;
    private final String evaluationReason;

    public FeatureFlagEvaluatedEvent(FeatureFlagId flagId, String flagName, Environment environment,
                                     String userId, boolean result, String evaluationReason) {
        super(flagId.value(), "FeatureFlag", "system");
        this.flagId = flagId;
        this.flagName = flagName;
        this.environment = environment;
        this.userId = userId;
        this.result = result;
        this.evaluationReason = evaluationReason;
    }

    @Override
    public String getEventType() {
        return "FeatureFlagEvaluated";
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

    public String getUserId() {
        return userId;
    }

    public boolean getResult() {
        return result;
    }

    public String getEvaluationReason() {
        return evaluationReason;
    }
}
