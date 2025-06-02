package com.markin.togglefox.port.in;

import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.dto.command.CreateFlagCommand;
import com.markin.togglefox.domain.model.FeatureFlagId;

public interface CreateFlagUseCase {
    /**
     * Create a new feature flag
     */
    FeatureFlag createFlag(CreateFlagCommand command);
}
