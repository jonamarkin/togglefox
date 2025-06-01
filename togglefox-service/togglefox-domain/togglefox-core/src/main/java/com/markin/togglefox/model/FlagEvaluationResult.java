package com.markin.togglefox.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record FlagEvaluationResult(boolean enabled, String reason, Map<String, Object> metadata) {
    public FlagEvaluationResult {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static FlagEvaluationResult enabled(String reason) {
        return new FlagEvaluationResult(true, reason, Map.of());
    }

    public static FlagEvaluationResult enabled(String reason, Map<String, Object> metadata) {
        return new FlagEvaluationResult(true, reason, metadata);
    }

    public static FlagEvaluationResult disabled(String reason) {
        return new FlagEvaluationResult(false, reason, Map.of());
    }

    public static FlagEvaluationResult disabled(String reason, Map<String, Object> metadata) {
        return new FlagEvaluationResult(false, reason, metadata);
    }
}

