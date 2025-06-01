package com.markin.togglefox.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EvaluationContext {
    private final String userId;
    private final Environment environment;
    private final Map<String, Object> attributes;

    private EvaluationContext(String userId, Environment environment, Map<String, Object> attributes) {
        this.userId = userId;  // Can be null for anonymous users
        this.environment = Objects.requireNonNull(environment, "Environment cannot be null");
        this.attributes = new HashMap<>(attributes);
    }

    public static EvaluationContext of(String userId, Environment environment) {
        return new EvaluationContext(userId, environment, Map.of());
    }

    public static EvaluationContext of(String userId, Environment environment, Map<String, Object> attributes) {
        return new EvaluationContext(userId, environment, attributes);
    }

    public static EvaluationContext anonymous(Environment environment) {
        return new EvaluationContext(null, environment, Map.of());
    }

    public String getUserId() {
        return userId;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean isAnonymous() {
        return userId == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationContext)) return false;
        EvaluationContext that = (EvaluationContext) o;
        return Objects.equals(userId, that.userId) &&
                environment.equals(that.environment) &&
                Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, environment, attributes);
    }

    @Override
    public String toString() {
        return "EvaluationContext{" +
                "userId='" + userId + '\'' +
                ", environment=" + environment +
                ", attributes=" + attributes +
                '}';
    }
}
