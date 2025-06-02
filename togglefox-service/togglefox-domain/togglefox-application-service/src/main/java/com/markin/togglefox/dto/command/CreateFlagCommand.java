package com.markin.togglefox.dto.command;

import com.markin.togglefox.domain.model.Environment;

import java.util.Map;
import java.util.Objects;

public class CreateFlagCommand {

    private final String name;
    private final String description;
    private final String environment;
    private final String strategyType;
    private final Map<String, Object> strategyConfig;

    public CreateFlagCommand(String name, String description, String environment,
                             String strategyType, Map<String, Object> strategyConfig) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = description;
        this.environment = Objects.requireNonNull(environment, "Environment cannot be null");
        this.strategyType = Objects.requireNonNull(strategyType, "Strategy type cannot be null");
        this.strategyConfig = strategyConfig;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public Map<String, Object> getStrategyConfig() {
        return strategyConfig;
    }

    @Override
    public String toString() {
        return "CreateFlagCommand{" +
                "name='" + name + '\'' +
                ", environment='" + environment + '\'' +
                ", strategyType='" + strategyType + '\'' +
                '}';
    }
}
