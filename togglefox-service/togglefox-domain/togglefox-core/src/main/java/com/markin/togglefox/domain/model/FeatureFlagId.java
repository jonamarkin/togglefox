package com.markin.togglefox.domain.model;

import java.util.Objects;
import java.util.UUID;

public class FeatureFlagId {

    private final String value;

    private FeatureFlagId(String value) {
        this.value = Objects.requireNonNull(value, "FeatureFlagId value cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("FeatureFlagId value cannot be empty");
        }
    }

    public static FeatureFlagId of(String value) {
        return new FeatureFlagId(value);
    }

    public static FeatureFlagId generate() {
        return new FeatureFlagId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureFlagId that = (FeatureFlagId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FeatureFlagId{" + value + '}';
    }
}