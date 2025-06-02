package com.markin.togglefox.domain.strategy;

import com.markin.togglefox.domain.model.EvaluationContext;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.model.FlagEvaluationResult;

import java.util.*;

public class UserTargetingStrategy implements RolloutStrategy {
    private final FeatureFlagId flagId;
    private final Set<String> targetedUsers;

    public UserTargetingStrategy(FeatureFlagId flagId, Set<String> targetedUsers) {
        this.flagId = Objects.requireNonNull(flagId, "Flag ID cannot be null");
        this.targetedUsers = new HashSet<>(Objects.requireNonNull(targetedUsers, "Targeted users cannot be null"));
    }

    @Override
    public FlagEvaluationResult evaluate(EvaluationContext context) {
        if (context.isAnonymous()) {
            return FlagEvaluationResult.disabled(flagId, "Anonymous users cannot be targeted");
        }

        boolean enabled = targetedUsers.contains(context.getUserId());
        String reason = enabled
                ? "User is in targeted user list"
                : "User is not in targeted user list";

        return enabled
                ? FlagEvaluationResult.enabled(flagId, reason)
                : FlagEvaluationResult.disabled(flagId, reason);
    }

    @Override
    public String getStrategyType() {
        return "USER_TARGETING";
    }

    @Override
    public boolean isValid() {
        return targetedUsers != null && !targetedUsers.isEmpty();
    }

    public Set<String> getTargetedUsers() {
        return new HashSet<>(targetedUsers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTargetingStrategy that = (UserTargetingStrategy) o;
        return Objects.equals(flagId, that.flagId) && Objects.equals(targetedUsers, that.targetedUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flagId, targetedUsers);
    }

    @Override
    public String toString() {
        return "UserTargetingStrategy{" +
                "flagId=" + flagId +
                ", targetedUsers=" + targetedUsers +
                '}';
    }
}
