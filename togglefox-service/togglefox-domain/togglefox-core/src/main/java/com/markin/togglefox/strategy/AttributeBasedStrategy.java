package com.markin.togglefox.strategy;

import com.markin.togglefox.model.EvaluationContext;
import com.markin.togglefox.model.FlagEvaluationResult;

import java.util.Map;
import java.util.Objects;

public class AttributeBasedStrategy implements RolloutStrategy {

    private final String attributeKey;
    private final Object expectedValue;
    private final ComparisonOperator operator;

    public enum ComparisonOperator {
        EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, CONTAINS
    }

    public AttributeBasedStrategy(String attributeKey, Object expectedValue, ComparisonOperator operator) {
        this.attributeKey = Objects.requireNonNull(attributeKey, "Attribute key cannot be null");
        this.expectedValue = expectedValue;
        this.operator = Objects.requireNonNull(operator, "Operator cannot be null");
    }

    @Override
    public FlagEvaluationResult evaluate(EvaluationContext context) {
        Object actualValue = context.getAttribute(attributeKey);
        boolean matches = evaluateCondition(actualValue, expectedValue, operator);

        Map<String, Object> metadata = Map.of(
                "strategy", "attribute_based",
                "attributeKey", attributeKey,
                "expectedValue", expectedValue,
                "actualValue", actualValue,
                "operator", operator.name()
        );

        String reason = String.format("Attribute '%s' %s evaluation",
                attributeKey, matches ? "passed" : "failed");

        return matches ?
                FlagEvaluationResult.enabled(reason, metadata) :
                FlagEvaluationResult.disabled(reason, metadata);
    }

    private boolean evaluateCondition(Object actual, Object expected, ComparisonOperator op) {
        if (actual == null) {
            return op == ComparisonOperator.NOT_EQUALS && expected != null;
        }

        switch (op) {
            case EQUALS:
                return Objects.equals(actual, expected);
            case NOT_EQUALS:
                return !Objects.equals(actual, expected);
            case GREATER_THAN:
                return compareNumbers(actual, expected) > 0;
            case LESS_THAN:
                return compareNumbers(actual, expected) < 0;
            case CONTAINS:
                return actual.toString().contains(expected.toString());
            default:
                return false;
        }
    }

    private int compareNumbers(Object actual, Object expected) {
        if (actual instanceof Number && expected instanceof Number) {
            double actualDouble = ((Number) actual).doubleValue();
            double expectedDouble = ((Number) expected).doubleValue();
            return Double.compare(actualDouble, expectedDouble);
        }
        throw new IllegalArgumentException("Cannot compare non-numeric values");
    }

    @Override
    public String getType() {
        return "attribute_based";
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeBasedStrategy that = (AttributeBasedStrategy) o;
        return Objects.equals(attributeKey, that.attributeKey) &&
                Objects.equals(expectedValue, that.expectedValue) &&
                operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeKey, expectedValue, operator);
    }

    @Override
    public String toString() {
        return "AttributeBasedStrategy{" +
                "attributeKey='" + attributeKey + '\'' +
                ", expectedValue=" + expectedValue +
                ", operator=" + operator +
                '}';
    }
}
