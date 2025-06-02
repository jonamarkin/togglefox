package com.markin.togglefox.dto.command;

import com.markin.togglefox.domain.model.FeatureFlagId;

import java.util.Map;
import java.util.Objects;

public class UpdateStrategyCommand {

    private final String flagId;
    private final String strategyType;
    private final Map<String, Object> strategyConfig;

    public UpdateStrategyCommand(String flagId, String strategyType, Map<String, Object> strategyConfig) {
        this.flagId = Objects.requireNonNull(flagId, "Flag ID cannot be null");
        this.strategyType = Objects.requireNonNull(strategyType, "Strategy type cannot be null");
        this.strategyConfig = strategyConfig;
    }

    public String getFlagId() { return flagId; }
    public String getStrategyType() { return strategyType; }
    public Map<String, Object> getStrategyConfig() { return strategyConfig; }

    @Override
    public String toString() {
        return "UpdateStrategyCommand{" +
                "flagId='" + flagId + '\'' +
                ", strategyType='" + strategyType + '\'' +
                '}';
    }
}