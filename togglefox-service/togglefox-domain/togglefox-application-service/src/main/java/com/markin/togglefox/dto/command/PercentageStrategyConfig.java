package com.markin.togglefox.dto.command;

public class PercentageStrategyConfig {
    private final int percentage;

    public PercentageStrategyConfig(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        this.percentage = percentage;
    }
    public int getPercentage() {
        return percentage;
    }
}
