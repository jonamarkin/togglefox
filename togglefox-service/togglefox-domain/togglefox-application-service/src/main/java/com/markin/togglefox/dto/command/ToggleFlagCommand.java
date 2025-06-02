package com.markin.togglefox.dto.command;

import com.markin.togglefox.domain.model.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;

public record ToggleFlagCommand(
        FeatureFlagId flagId,
        boolean enabled,
        String updatedBy,
        String reason,
        LocalDateTime requestedAt
) {

    public ToggleFlagCommand {
        Objects.requireNonNull(flagId, "Flag ID cannot be null");
        Objects.requireNonNull(updatedBy, "Updated by cannot be null");

        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
    }

    // Convenience constructors as static factory methods
    public static ToggleFlagCommand enableFlag(FeatureFlagId flagId, String updatedBy) {
        return new ToggleFlagCommand(flagId, true, updatedBy, null, LocalDateTime.now());
    }

    public static ToggleFlagCommand enableFlag(FeatureFlagId flagId, String updatedBy, String reason) {
        return new ToggleFlagCommand(flagId, true, updatedBy, reason, LocalDateTime.now());
    }

    public static ToggleFlagCommand disableFlag(FeatureFlagId flagId, String updatedBy) {
        return new ToggleFlagCommand(flagId, false, updatedBy, null, LocalDateTime.now());
    }

    public static ToggleFlagCommand disableFlag(FeatureFlagId flagId, String updatedBy, String reason) {
        return new ToggleFlagCommand(flagId, false, updatedBy, reason, LocalDateTime.now());
    }

    // Utility methods
    public boolean isEnableOperation() {
        return enabled;
    }

    public boolean isDisableOperation() {
        return !enabled;
    }

}
