package com.markin.togglefox.domain.strategy;

import com.markin.togglefox.domain.model.EvaluationContext;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.model.FlagEvaluationResult;

import java.util.*;

public class AttributeBasedStrategy implements RolloutStrategy {

    private final FeatureFlagId flagId;
    private final Map<String, Set<Object>> attributeRules;

    public AttributeBasedStrategy(FeatureFlagId flagId, Map<String, Set<Object>> attributeRules) {
        this.flagId = Objects.requireNonNull(flagId, "Flag ID cannot be null");
        this.attributeRules = new HashMap<>();
        if (attributeRules != null) {
            attributeRules.forEach((key, values) ->
                    this.attributeRules.put(key, new HashSet<>(values)));
        }
    }

    @Override
    public FlagEvaluationResult evaluate(EvaluationContext context) {
        if (attributeRules.isEmpty()) {
            return FlagEvaluationResult.disabled(flagId, "No attribute rules configured");
        }

        List<String> matchedRules = new ArrayList<>();
        List<String> failedRules = new ArrayList<>();

        for (Map.Entry<String, Set<Object>> rule : attributeRules.entrySet()) {
            String attributeKey = rule.getKey();
            Set<Object> expectedValues = rule.getValue();

            if (!context.hasAttribute(attributeKey)) {
                failedRules.add("Missing attribute: " + attributeKey);
                continue;
            }

            Object actualValue = context.getAttribute(attributeKey);
            if (expectedValues.contains(actualValue)) {
                matchedRules.add(attributeKey + "=" + actualValue);
            } else {
                failedRules.add(attributeKey + "=" + actualValue + " not in " + expectedValues);
            }
        }

        boolean allRulesMatched = failedRules.isEmpty();
        String reason = allRulesMatched
                ? "All attribute rules matched: " + matchedRules
                : "Some attribute rules failed: " + failedRules;

        return allRulesMatched
                ? FlagEvaluationResult.enabled(flagId, reason)
                : FlagEvaluationResult.disabled(flagId, reason);
    }

    @Override
    public String getStrategyType() {
        return "ATTRIBUTE_BASED";
    }

    @Override
    public boolean isValid() {
        return attributeRules != null && !attributeRules.isEmpty();
    }

    public Map<String, Set<Object>> getAttributeRules() {
        Map<String, Set<Object>> copy = new HashMap<>();
        attributeRules.forEach((key, values) -> copy.put(key, new HashSet<>(values)));
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeBasedStrategy that = (AttributeBasedStrategy) o;
        return Objects.equals(flagId, that.flagId) && Objects.equals(attributeRules, that.attributeRules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flagId, attributeRules);
    }

    @Override
    public String toString() {
        return "AttributeBasedStrategy{" +
                "flagId=" + flagId +
                ", attributeRules=" + attributeRules +
                '}';
    }
}
