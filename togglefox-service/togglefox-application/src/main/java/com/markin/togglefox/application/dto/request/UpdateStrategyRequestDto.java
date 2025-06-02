package com.markin.togglefox.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(description = "Request to update rollout strategy")
public class UpdateStrategyRequestDto {

    @NotBlank(message = "Strategy type is required")
    @Schema(description = "Rollout strategy type", example = "PERCENTAGE",
            allowableValues = {"PERCENTAGE", "USER_TARGETING", "ATTRIBUTE_BASED"})
    private String strategyType;

    @NotNull(message = "Strategy configuration is required")
    @Schema(description = "Strategy configuration parameters")
    private Map<String, Object> strategyConfig;

    // Default constructor
    public UpdateStrategyRequestDto() {}

    public UpdateStrategyRequestDto(String strategyType, Map<String, Object> strategyConfig) {
        this.strategyType = strategyType;
        this.strategyConfig = strategyConfig;
    }

    // Getters and setters
    public String getStrategyType() { return strategyType; }
    public void setStrategyType(String strategyType) { this.strategyType = strategyType; }

    public Map<String, Object> getStrategyConfig() { return strategyConfig; }
    public void setStrategyConfig(Map<String, Object> strategyConfig) { this.strategyConfig = strategyConfig; }
}
