package com.markin.togglefox.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

@Schema(description = "Request to create a new feature flag")
public class CreateFlagRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Name can only contain alphanumeric characters, dots, underscores, and hyphens")
    @Schema(description = "Feature flag name", example = "new-checkout-flow")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Feature flag description", example = "Enable the new checkout flow for users")
    private String description;

    @NotBlank(message = "Environment is required")
    @Schema(description = "Target environment", example = "production")
    private String environment;

    @NotBlank(message = "Strategy type is required")
    @Schema(description = "Rollout strategy type", example = "PERCENTAGE",
            allowableValues = {"PERCENTAGE", "USER_TARGETING", "ATTRIBUTE_BASED"})
    private String strategyType;

    @NotNull(message = "Strategy configuration is required")
    @Schema(description = "Strategy configuration parameters")
    private Map<String, Object> strategyConfig;

    // Default constructor
    public CreateFlagRequestDto() {}

    public CreateFlagRequestDto(String name, String description, String environment,
                                String strategyType, Map<String, Object> strategyConfig) {
        this.name = name;
        this.description = description;
        this.environment = environment;
        this.strategyType = strategyType;
        this.strategyConfig = strategyConfig;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getStrategyType() { return strategyType; }
    public void setStrategyType(String strategyType) { this.strategyType = strategyType; }

    public Map<String, Object> getStrategyConfig() { return strategyConfig; }
    public void setStrategyConfig(Map<String, Object> strategyConfig) { this.strategyConfig = strategyConfig; }
}
