package com.markin.togglefox.strategy;

import com.markin.togglefox.valueobject.EvaluationContext;
import com.markin.togglefox.valueobject.FlagEvaluationResult;

import java.util.*;

public class UserTargetingStrategy implements RolloutStrategy {
    private final Set<String> targetedUsers;
    private final boolean whitelist; // true for whitelist, false for blacklist

    public UserTargetingStrategy(Set<String> targetedUsers, boolean whitelist) {
        this.targetedUsers = new HashSet<>(Objects.requireNonNull(targetedUsers, "Targeted users cannot be null"));
        this.whitelist = whitelist;
    }

    public static UserTargetingStrategy whitelist(Set<String> users) {
        return new UserTargetingStrategy(users, true);
    }

    public static UserTargetingStrategy blacklist(Set<String> users) {
        return new UserTargetingStrategy(users, false);
    }

    @Override
    public FlagEvaluationResult evaluate(EvaluationContext context) {
        if (context.isAnonymous()) {
            return FlagEvaluationResult.disabled("Anonymous users not supported for user targeting");
        }

        boolean userInList = targetedUsers.contains(context.getUserId());
        boolean enabled = whitelist ? userInList : !userInList;

        Map<String, Object> metadata = Map.of("strategy", "user_targeting", "whitelist", whitelist, "userInList", userInList, "targetedUserCount", targetedUsers.size());

        String reason;
        if (whitelist) {
            reason = userInList ? "User is in whitelist" : "User not in whitelist";
        } else {
            reason = userInList ? "User is in blacklist" : "User not in blacklist";
        }

        return enabled ? FlagEvaluationResult.enabled(reason, metadata) : FlagEvaluationResult.disabled(reason, metadata);
    }

    @Override
    public String getType() {
        return "user_targeting";
    }

    public Set<String> getTargetedUsers() {
        return Collections.unmodifiableSet(targetedUsers);
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTargetingStrategy that = (UserTargetingStrategy) o;
        return whitelist == that.whitelist && Objects.equals(targetedUsers, that.targetedUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetedUsers, whitelist);
    }

    @Override
    public String toString() {
        return "UserTargetingStrategy{" + "targetedUsers=" + targetedUsers.size() + " users" + ", whitelist=" + whitelist + '}';
    }
}
