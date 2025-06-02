package com.markin.togglefox.domain.model;

import java.util.Objects;

public class FlagEvaluationResult {

    private final FeatureFlagId flagId;
    private final boolean enabled;
    private final String reason;
    private final Object variation;

    private FlagEvaluationResult(FeatureFlagId flagId, boolean enabled, String reason, Object variation) {
        this.flagId = Objects.requireNonNull(flagId, "Flag ID cannot be null");
        this.enabled = enabled;
        this.reason = reason;
        this.variation = variation;
    }

    public static FlagEvaluationResult enabled(FeatureFlagId flagId, String reason) {
        return new FlagEvaluationResult(flagId, true, reason, null);
    }

    public static FlagEvaluationResult disabled(FeatureFlagId flagId, String reason) {
        return new FlagEvaluationResult(flagId, false, reason, null);
    }

    public static FlagEvaluationResult withVariation(FeatureFlagId flagId, boolean enabled,
                                                     String reason, Object variation) {
        return new FlagEvaluationResult(flagId, enabled, reason, variation);
    }

    public FeatureFlagId getFlagId() {
        return flagId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getReason() {
        return reason;
    }

    public Object getVariation() {
        return variation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlagEvaluationResult that = (FlagEvaluationResult) o;
        return enabled == that.enabled &&
                Objects.equals(flagId, that.flagId) &&
                Objects.equals(reason, that.reason) &&
                Objects.equals(variation, that.variation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flagId, enabled, reason, variation);
    }

    @Override
    public String toString() {
        return "FlagEvaluationResult{" +
                "flagId=" + flagId +
                ", enabled=" + enabled +
                ", reason='" + reason + '\'' +
                ", variation=" + variation +
                '}';
    }
}