package com.markin.togglefox.port.in;

import com.markin.togglefox.dto.command.ToggleFlagCommand;

public interface ManageFlagUseCase {

    /**
     * Use case for managing feature flag state (enable/disable)
     * Used by administrators to control flag availability
     */

    /**
     * Enable a feature flag in a specific environment
     */
    void enableFlag(ToggleFlagCommand command);

    /**
     * Disable a feature flag in a specific environment
     */
    void disableFlag(ToggleFlagCommand command);
}
