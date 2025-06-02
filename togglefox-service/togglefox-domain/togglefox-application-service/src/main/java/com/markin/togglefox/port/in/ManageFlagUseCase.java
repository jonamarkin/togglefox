package com.markin.togglefox.port.in;

import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.dto.command.EnableFlagCommand;
import com.markin.togglefox.dto.command.ToggleFlagCommand;
import com.markin.togglefox.dto.command.UpdateStrategyCommand;

import java.util.Optional;

public interface ManageFlagUseCase {

    /**
     * Enable a feature flag
     */
    void enableFlag(EnableFlagCommand command);

    /**
     * Disable a feature flag
     */
    void disableFlag(FeatureFlagId flagId);

    /**
     * Update rollout strategy
     */
    void updateStrategy(UpdateStrategyCommand command);

    /**
     * Get a feature flag by ID
     */
    Optional<FeatureFlag> getFlag(FeatureFlagId flagId);
}
