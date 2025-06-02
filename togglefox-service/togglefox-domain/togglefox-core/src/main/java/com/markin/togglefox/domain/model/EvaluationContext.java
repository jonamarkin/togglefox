package com.markin.togglefox.domain.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EvaluationContext {

    private final String userId;
    private final Map<String, Object> attributes;

    private EvaluationContext(String userId, Map<String, Object> attributes) {
        this.userId = userId;
        this.attributes = new HashMap<>(attributes);
    }

    public static EvaluationContext forUser(String userId) {
        return new EvaluationContext(userId, new HashMap<>());
    }

    public static EvaluationContext forUserWithAttributes(String userId, Map<String, Object> attributes) {
        return new EvaluationContext(userId, attributes);
    }

    public static EvaluationContext anonymous() {
        return new EvaluationContext(null, new HashMap<>());
    }

    public EvaluationContext withAttribute(String key, Object value) {
        Map<String, Object> newAttributes = new HashMap<>(this.attributes);
        newAttributes.put(key, value);
        return new EvaluationContext(this.userId, newAttributes);
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public boolean isAnonymous() {
        return userId == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvaluationContext that = (EvaluationContext) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, attributes);
    }

    @Override
    public String toString() {
        return "EvaluationContext{" +
                "userId='" + userId + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
