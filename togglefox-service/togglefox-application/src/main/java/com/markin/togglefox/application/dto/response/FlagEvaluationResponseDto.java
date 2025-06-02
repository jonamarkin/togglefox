package com.markin.togglefox.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Feature flag evaluation result")
public class FlagEvaluationResponseDto {

    @Schema(description = "Feature flag ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String flagId;

    @Schema(description = "Feature flag name", example = "new-checkout-flow")
    private String flagName;

    @Schema(description = "Whether the flag is enabled for this evaluation", example = "true")
    private boolean enabled;

    @Schema(description = "Reason for the evaluation result", example = "User in 25% rollout (hash: 15)")
    private String reason;

    @Schema(description = "Variation value (if applicable)")
    private Object variation;

    // Default constructor
    public FlagEvaluationResponseDto() {
    }

    public FlagEvaluationResponseDto(String flagId, String flagName, boolean enabled, String reason, Object variation) {
        this.flagId = flagId;
        this.flagName = flagName;
        this.enabled = enabled;
        this.reason = reason;
        this.variation = variation;
    }

    // Getters and setters
    public String getFlagId() {
        return flagId;
    }

    public void setFlagId(String flagId) {
        this.flagId = flagId;
    }

    public String getFlagName() {
        return flagName;
    }

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getVariation() {
        return variation;
    }

    public void setVariation(Object variation) {
        this.variation = variation;
    }
}
