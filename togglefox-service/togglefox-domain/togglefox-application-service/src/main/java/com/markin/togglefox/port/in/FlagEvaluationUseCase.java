package com.markin.togglefox.port.in;

import com.markin.togglefox.model.EvaluationContext;
import com.markin.togglefox.model.FlagEvaluationResult;

public interface FlagEvaluationUseCase {
    /**
     * Evaluates a feature flag based on the provided context.
     *
     * @param flagName the name of the feature flag to evaluate
     * @param context  the evaluation context containing user and environment details
     * @return the result of the flag evaluation, indicating whether the flag is enabled or not
     */
    FlagEvaluationResult evaluateFlag(String flagName, EvaluationContext context);


}
