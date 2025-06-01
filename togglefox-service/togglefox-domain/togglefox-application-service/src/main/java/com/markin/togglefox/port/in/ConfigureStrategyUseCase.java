package com.markin.togglefox.port.in;

import com.markin.togglefox.dto.command.UpdateStrategyCommand;

public interface ConfigureStrategyUseCase {

    /**
     * Use case for configuring rollout strategies
     * Used by product managers to define rollout rules
     */

    /**
     * Update the rollout strategy of a feature flag
     */
    void updateRolloutStrategy(UpdateStrategyCommand command);

    /**
     * Remove rollout strategy
     */
    void removeRolloutStrategy(String flagName, String environment, String updatedBy);
}
