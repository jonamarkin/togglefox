package com.markin.togglefox.valueobject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FlagEvaluationResult {
    private final boolean enabled;
    private final String reason;
    private final Map<String, Object> metadata;

    private FlagEvaluationResult(boolean enabled, String reason, Map<String, Object> metadata) {
        this.enabled = enabled;
        this.reason = reason;
        this.metadata = new HashMap<>(metadata);
    }

    public static FlagEvaluationResult enabled(String reason) {
        return new FlagEvaluationResult(true, reason, Map.of());
    }

    public static FlagEvaluationResult enabled(String reason, Map<String, Object> metadata) {
        return new FlagEvaluationResult(true, reason, metadata);
    }

    public static FlagEvaluationResult disabled(String reason) {
        return new FlagEvaluationResult(false, reason, Map.of());
    }

    public static FlagEvaluationResult disabled(String reason, Map<String, Object> metadata) {
        return new FlagEvaluationResult(false, reason, metadata);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getReason() {
        return reason;
    }

    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlagEvaluationResult that = (FlagEvaluationResult) o;
        return enabled == that.enabled &&
                Objects.equals(reason, that.reason) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, reason, metadata);
    }

    @Override
    public String toString() {
        return "FlagEvaluationResult{" +
                "enabled=" + enabled +
                ", reason='" + reason + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
