package com.markin.togglefox.valueobject;

import java.util.Objects;

public class FeatureFlagId {
    private final String value;

    private FeatureFlagId(String value) {
        this.value = Objects.requireNonNull(value, "FeatureFlagId value cannot be null");
    }

    public static FeatureFlagId of(String value) {
        return new FeatureFlagId(value);
    }

    public static FeatureFlagId generate() {
        return new FeatureFlagId(java.util.UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureFlagId)) return false;
        FeatureFlagId that = (FeatureFlagId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
