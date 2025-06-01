package com.markin.togglefox.valueobject;

import java.util.Objects;

public class Environment {
    private final String name;

    // Pre-defined environments
    public static final Environment DEVELOPMENT = new Environment("development");
    public static final Environment STAGING = new Environment("staging");
    public static final Environment PRODUCTION = new Environment("production");

    private Environment(String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Environment name cannot be null or empty");
        }
        this.name = name.trim().toLowerCase();
    }

    public static Environment of(String name) {
        return new Environment(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Environment)) return false;
        Environment that = (Environment) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
