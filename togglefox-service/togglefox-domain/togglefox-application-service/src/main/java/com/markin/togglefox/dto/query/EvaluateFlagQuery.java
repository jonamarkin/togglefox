package com.markin.togglefox.dto.query;

import java.util.Map;
import java.util.Objects;

public class EvaluateFlagQuery {
    private final String flagName;
    private final String environment;
    private final String userId;
    private final Map<String, Object> attributes;

    public EvaluateFlagQuery(String flagName, String environment, String userId, Map<String, Object> attributes) {
        this.flagName = Objects.requireNonNull(flagName, "Flag name cannot be null");
        this.environment = Objects.requireNonNull(environment, "Environment cannot be null");
        this.userId = userId;
        this.attributes = attributes;
    }

    public String getFlagName() {
        return flagName;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "EvaluateFlagQuery{" +
                "flagName='" + flagName + '\'' +
                ", environment='" + environment + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
