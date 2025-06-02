package com.markin.togglefox.domain.model;

import java.util.Objects;

public class Environment {

    private final String name;

    private Environment(String name) {
        this.name = validateName(name);
    }

    public static Environment of(String name) {
        return new Environment(name);
    }

    // Predefined environments
    public static Environment development() {
        return new Environment("development");
    }

    public static Environment staging() {
        return new Environment("staging");
    }

    public static Environment production() {
        return new Environment("production");
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Environment name cannot be null or empty");
        }
        if (!name.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Environment name can only contain alphanumeric characters, underscores, and hyphens");
        }
        return name.toLowerCase().trim();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Environment that = (Environment) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Environment{" + name + '}';
    }
}