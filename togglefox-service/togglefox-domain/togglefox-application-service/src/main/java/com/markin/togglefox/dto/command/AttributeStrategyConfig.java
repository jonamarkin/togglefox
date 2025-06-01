package com.markin.togglefox.dto.command;

import com.markin.togglefox.strategy.AttributeBasedStrategy;

import java.util.Objects;

public record AttributeStrategyConfig(
        String key,
        Object value,
        AttributeBasedStrategy.ComparisonOperator operator
) {
    public AttributeStrategyConfig {
        Objects.requireNonNull(key, "Attribute key cannot be null");
        Objects.requireNonNull(value, "Attribute value cannot be null");
        Objects.requireNonNull(operator, "Comparison operator cannot be null");
    }
}
