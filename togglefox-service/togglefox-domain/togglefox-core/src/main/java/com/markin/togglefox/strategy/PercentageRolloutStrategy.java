package com.markin.togglefox.strategy;

import com.markin.togglefox.util.UserHashUtil;
import com.markin.togglefox.valueobject.EvaluationContext;
import com.markin.togglefox.valueobject.FlagEvaluationResult;

import java.util.Map;
import java.util.Objects;

public class PercentageRolloutStrategy implements RolloutStrategy {

    private final int percentage;

    public PercentageRolloutStrategy(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        this.percentage = percentage;
    }

    @Override
    public FlagEvaluationResult evaluate(EvaluationContext context) {
        //For anonymous users, we can use a random number generator to simulate the percentage rollout
        if (context.isAnonymous()) {
            int randomValue = (int) (Math.random() * 100);
            if (randomValue < percentage) {
                return FlagEvaluationResult.enabled("User is part of the rollout");
            } else {
                return FlagEvaluationResult.disabled("User is not part of the rollout");
            }
        } else {
            // For authenticated users, we can use consistent hashing based on user ID
            int userHash = UserHashUtil.hashUser(context.getUserId());
            boolean enabled = UserHashUtil.isUserInPercentage(context.getUserId(), percentage);

            Map<String, Object> metadata = Map.of(
                    "userHash", userHash,
                    "percentage", percentage,
                    "strategy", "percentage"
            );

            if(enabled){
                return FlagEvaluationResult.enabled("User is part of the %d%% rollout".formatted(percentage), metadata);
            }else {
                return FlagEvaluationResult.disabled("User is not part of the %d%% rollout".formatted(percentage), metadata);
            }

        }
    }

    @Override
    public String getType() {
        return "percentage";
    }

    public int getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PercentageRolloutStrategy)) return false;
        PercentageRolloutStrategy that = (PercentageRolloutStrategy) o;
        return percentage == that.percentage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage);
    }

    @Override
    public String toString() {
        return "PercentageRolloutStrategy{" +
                "percentage=" + percentage +
                '}';
    }
}
