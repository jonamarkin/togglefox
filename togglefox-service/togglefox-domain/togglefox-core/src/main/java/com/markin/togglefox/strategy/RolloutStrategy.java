package com.markin.togglefox.strategy;

import com.markin.togglefox.valueobject.EvaluationContext;
import com.markin.togglefox.valueobject.FlagEvaluationResult;

public interface RolloutStrategy {
    FlagEvaluationResult evaluate(EvaluationContext context);
    String getType();
}
