package com.markin.togglefox.port.in;

import com.markin.togglefox.dto.command.CreateFlagCommand;
import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.EvaluationContext;
import com.markin.togglefox.model.FeatureFlag;
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

    /**
     * Create a new feature flag with the specified details.
     * @param command the command containing the details for the new feature flag
     * @return the FeatureFlag created
     */
    FeatureFlag createFlag(CreateFlagCommand command);

    /**
     * Enable a feature flag
     * @param flagName the name of the feature flag to enable
     * @param environment the environment in which to enable the flag
     *
     */
    void enableFlag(String flagName, String environment);

    /**
     * Disable a feature flag
     * @param flagName the name of the feature flag to disable
     * @param environment the environment in which to disable the flag
     */
    void disableFlag(String flagName, String environment);

    /**
     * Update the rollout strategy of a feature flag.
     * @param flagName the name of the feature flag to update
     * @param strategy the new rollout strategy to apply
     */
    void updateRolloutStrategy(String flagName, String strategy);

    /**
     * Get the details of a feature flag.
     * @param flagName the name of the feature flag to retrieve
     * @param environment the environment in which to retrieve the flag
     */
    FeatureFlag getFlag(String flagName, Environment environment);

}
