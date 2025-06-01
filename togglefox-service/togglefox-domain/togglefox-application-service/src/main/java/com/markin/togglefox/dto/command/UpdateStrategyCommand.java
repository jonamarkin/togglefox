package com.markin.togglefox.dto.command;

import com.markin.togglefox.model.FeatureFlagId;

import java.util.Objects;

public record UpdateStrategyCommand(
        FeatureFlagId flagId,
        String strategyType,
        Object strategyConfig,
        String updatedBy
) {
    public UpdateStrategyCommand {
        Objects.requireNonNull(flagId, "Flag ID cannot be null");
        Objects.requireNonNull(strategyType, "Strategy type cannot be null");
        Objects.requireNonNull(updatedBy, "Updated by cannot be null");
    }

    // Factory methods for different strategy types
    public static UpdateStrategyCommand forPercentageStrategy(FeatureFlagId flagId, int percentage, String updatedBy) {
        return new UpdateStrategyCommand(flagId, "percentage", new PercentageStrategyConfig(percentage), updatedBy);
    }

    public static UpdateStrategyCommand forUserTargeting(FeatureFlagId flagId, UserTargetingStrategyConfig config, String updatedBy) {
        return new UpdateStrategyCommand(flagId, "user_targeting", config, updatedBy);
    }
}
