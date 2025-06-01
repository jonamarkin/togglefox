package com.markin.togglefox.port.in;

import com.markin.togglefox.dto.command.CreateFlagCommand;
import com.markin.togglefox.model.FeatureFlagId;

public interface CreateFlagUseCase {
    /**
     * Creates a new feature flag with basic information
     * Flag starts disabled with no rollout strategy
     */
    FeatureFlagId createFlag(CreateFlagCommand command);
}
