package com.markin.togglefox.strategy;

import com.markin.togglefox.model.EvaluationContext;
import com.markin.togglefox.model.FlagEvaluationResult;

public interface RolloutStrategy {
    FlagEvaluationResult evaluate(EvaluationContext context);
    String getType();
}
