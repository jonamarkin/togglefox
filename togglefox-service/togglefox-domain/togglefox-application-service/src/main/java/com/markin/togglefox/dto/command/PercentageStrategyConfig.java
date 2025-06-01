package com.markin.togglefox.dto.command;

public record PercentageStrategyConfig(
        int percentage
) {
    public PercentageStrategyConfig {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100, got: " + percentage);
        }
    }
}
