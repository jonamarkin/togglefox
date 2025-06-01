package com.markin.togglefox.dto.command;

import com.markin.togglefox.model.FeatureFlagId;

import java.util.Objects;

public class UpdateStrategyCommand {
    private final FeatureFlagId flagId;
    private final String strategyType;     // "percentage", "user_targeting", "attribute_based"
    private final Object strategyConfig;   // Strategy-specific configuration
    private final String updatedBy;

    public UpdateStrategyCommand(FeatureFlagId flagId, String strategyType,
                                 Object strategyConfig, String updatedBy) {
        this.flagId = Objects.requireNonNull(flagId);
        this.strategyType = Objects.requireNonNull(strategyType);
        this.strategyConfig = strategyConfig;
        this.updatedBy = Objects.requireNonNull(updatedBy);
    }

    public FeatureFlagId getFlagId() {
        return flagId;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public Object getStrategyConfig() {
        return strategyConfig;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
