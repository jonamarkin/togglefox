package com.markin.togglefox.port.in;

import com.markin.togglefox.domain.model.EvaluationContext;
import com.markin.togglefox.domain.model.FlagEvaluationResult;
import com.markin.togglefox.dto.query.EvaluateFlagQuery;

public interface FlagEvaluationUseCase {
    /**
     * Evaluate a feature flag for the given context
     */
    FlagEvaluationResult evaluateFlag(EvaluateFlagQuery query);


}
