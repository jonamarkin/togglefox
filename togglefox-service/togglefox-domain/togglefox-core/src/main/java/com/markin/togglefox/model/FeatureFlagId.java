package com.markin.togglefox.model;

import java.util.Objects;
import java.util.UUID;

public record FeatureFlagId(
        String value
) {
    public FeatureFlagId {
        Objects.requireNonNull(value, "FeatureFlagId value cannot be null");
    }

    public static FeatureFlagId of(String value) {
        return new FeatureFlagId(value);
    }

    public static FeatureFlagId generate() {
        return new FeatureFlagId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
