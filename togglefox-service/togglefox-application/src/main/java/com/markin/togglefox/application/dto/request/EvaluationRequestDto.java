package com.markin.togglefox.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Request to evaluate a feature flag")
public class EvaluationRequestDto {

    @Schema(description = "User ID (optional for anonymous evaluation)", example = "user-123")
    private String userId;

    @Schema(description = "User attributes for targeting", example = "{\"country\": \"US\", \"plan\": \"premium\"}")
    private Map<String, Object> attributes;

    // Default constructor
    public EvaluationRequestDto() {}

    public EvaluationRequestDto(String userId, Map<String, Object> attributes) {
        this.userId = userId;
        this.attributes = attributes;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
