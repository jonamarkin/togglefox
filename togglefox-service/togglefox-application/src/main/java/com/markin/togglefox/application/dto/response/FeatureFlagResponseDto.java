package com.markin.togglefox.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Feature flag information")
public class FeatureFlagResponseDto {

    @Schema(description = "Feature flag ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "Feature flag name", example = "new-checkout-flow")
    private String name;

    @Schema(description = "Feature flag description", example = "Enable the new checkout flow for users")
    private String description;

    @Schema(description = "Whether the flag is enabled", example = "true")
    private boolean enabled;

    @Schema(description = "Target environment", example = "production")
    private String environment;

    @Schema(description = "Rollout strategy type", example = "PERCENTAGE")
    private String strategyType;

    @Schema(description = "Strategy configuration parameters")
    private Map<String, Object> strategyConfig;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    // Default constructor
    public FeatureFlagResponseDto() {
    }

    public FeatureFlagResponseDto(String id, String name, String description, boolean enabled,
                                  String environment, String strategyType, Map<String, Object> strategyConfig,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.environment = environment;
        this.strategyType = strategyType;
        this.strategyConfig = strategyConfig;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public Map<String, Object> getStrategyConfig() {
        return strategyConfig;
    }

    public void setStrategyConfig(Map<String, Object> strategyConfig) {
        this.strategyConfig = strategyConfig;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

