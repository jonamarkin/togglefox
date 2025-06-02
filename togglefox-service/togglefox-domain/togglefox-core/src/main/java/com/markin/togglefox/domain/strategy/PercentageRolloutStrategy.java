package com.markin.togglefox.domain.strategy;

import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.util.UserHashUtil;
import com.markin.togglefox.domain.model.EvaluationContext;
import com.markin.togglefox.domain.model.FlagEvaluationResult;

import java.util.Objects;

public class PercentageRolloutStrategy implements RolloutStrategy {

    private final int percentage;
    private final FeatureFlagId flagId;

    public PercentageRolloutStrategy(FeatureFlagId flagId, int percentage) {
        this.flagId = Objects.requireNonNull(flagId, "Flag ID cannot be null");
        this.percentage = validatePercentage(percentage);
    }

    @Override
    public FlagEvaluationResult evaluate(EvaluationContext context) {
        if (context.isAnonymous()) {
            return FlagEvaluationResult.disabled(flagId, "Anonymous users not supported for percentage rollout");
        }

        // Use consistent hashing to determine if user is in rollout
        int userHash = UserHashUtil.hashUser(context.getUserId(), flagId.getValue());
        boolean enabled = userHash < percentage;

        String reason = enabled
                ? "User in " + percentage + "% rollout (hash: " + userHash + ")"
                : "User not in " + percentage + "% rollout (hash: " + userHash + ")";

        return enabled
                ? FlagEvaluationResult.enabled(flagId, reason)
                : FlagEvaluationResult.disabled(flagId, reason);
    }

    @Override
    public String getStrategyType() {
        return "PERCENTAGE";
    }

    @Override
    public boolean isValid() {
        return percentage >= 0 && percentage <= 100;
    }

    private int validatePercentage(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        return percentage;
    }

    public int getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PercentageRolloutStrategy that = (PercentageRolloutStrategy) o;
        return percentage == that.percentage && Objects.equals(flagId, that.flagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage, flagId);
    }

    @Override
    public String toString() {
        return "PercentageRolloutStrategy{" +
                "percentage=" + percentage +
                ", flagId=" + flagId +
                '}';
    }
}
