package com.markin.togglefox.domain.strategy;

import com.markin.togglefox.domain.model.EvaluationContext;
import com.markin.togglefox.domain.model.FlagEvaluationResult;

public interface RolloutStrategy {
    /**
     * Evaluate the strategy for the given context
     */
    FlagEvaluationResult evaluate(EvaluationContext context);

    /**
     * Get the strategy type for serialization/persistence
     */
    String getStrategyType();

    /**
     * Validate strategy configuration
     */
    boolean isValid();
}
