package com.markin.togglefox.domain.model;

import com.markin.togglefox.domain.event.*;
import com.markin.togglefox.domain.strategy.RolloutStrategy;

import java.time.LocalDateTime;
import java.util.*;

public class FeatureFlag {
    private final FeatureFlagId id;
    private String name;
    private String description;
    private boolean enabled;
    private Environment environment;
    private RolloutStrategy rolloutStrategy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents;


    private FeatureFlag(FeatureFlagId id, String name, String description,
                        Environment environment, RolloutStrategy rolloutStrategy) {
        this.id = Objects.requireNonNull(id, "FeatureFlag ID cannot be null");
        this.name = validateName(name);
        this.description = description;
        this.environment = Objects.requireNonNull(environment, "Environment cannot be null");
        this.rolloutStrategy = Objects.requireNonNull(rolloutStrategy, "Rollout strategy cannot be null");
        this.enabled = false; // New flags are disabled by default
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domainEvents = new ArrayList<>();

        // Raise domain event
        this.domainEvents.add(new FeatureFlagCreatedEvent(id, name, environment));
    }

    /**
     * Factory method to create a new feature flag
     */
    public static FeatureFlag create(FeatureFlagId id, String name, String description,
                                     Environment environment, RolloutStrategy rolloutStrategy) {
        return new FeatureFlag(id, name, description, environment, rolloutStrategy);
    }

    /**
     * Factory method to reconstruct from persistence
     */
    public static FeatureFlag reconstruct(FeatureFlagId id, String name, String description,
                                          boolean enabled, Environment environment,
                                          RolloutStrategy rolloutStrategy, LocalDateTime createdAt,
                                          LocalDateTime updatedAt) {
        FeatureFlag flag = new FeatureFlag(id, name, description, environment, rolloutStrategy);
        flag.enabled = enabled;
        flag.updatedAt = updatedAt;
        // Clear events for reconstructed entities
        flag.domainEvents.clear();
        return flag;
    }

    /**
     * Enable the feature flag
     */
    public void enable() {
        if (!this.enabled) {
            this.enabled = true;
            this.updatedAt = LocalDateTime.now();
            this.domainEvents.add(new FeatureFlagEnabledEvent(this.id, this.name));
        }
    }

    /**
     * Disable the feature flag
     */
    public void disable() {
        if (this.enabled) {
            this.enabled = false;
            this.updatedAt = LocalDateTime.now();
            this.domainEvents.add(new FeatureFlagDisabledEvent(this.id, this.name));
        }
    }

    /**
     * Update the rollout strategy
     */
    public void updateStrategy(RolloutStrategy newStrategy) {
        Objects.requireNonNull(newStrategy, "Rollout strategy cannot be null");

        if (!this.rolloutStrategy.equals(newStrategy)) {
            RolloutStrategy oldStrategy = this.rolloutStrategy;
            this.rolloutStrategy = newStrategy;
            this.updatedAt = LocalDateTime.now();
            this.domainEvents.add(new FeatureFlagStrategyUpdatedEvent(
                    this.id, this.name, oldStrategy, newStrategy));
        }
    }

    /**
     * Evaluate the feature flag for a given context
     */
    public FlagEvaluationResult evaluate(EvaluationContext context) {
        if (!this.enabled) {
            return FlagEvaluationResult.disabled(this.id, "Feature flag is disabled");
        }

        return this.rolloutStrategy.evaluate(context);
    }

    /**
     * Update basic information
     */
    public void updateInfo(String name, String description) {
        this.name = validateName(name);
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get and clear domain events (for event publishing)
     */
    public List<DomainEvent> getAndClearDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    // Validation
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Feature flag name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Feature flag name cannot exceed 100 characters");
        }
        if (!name.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException("Feature flag name can only contain alphanumeric characters, dots, underscores, and hyphens");
        }
        return name.trim();
    }

    // Getters
    public FeatureFlagId getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public Environment getEnvironment() { return environment; }
    public RolloutStrategy getRolloutStrategy() { return rolloutStrategy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureFlag that = (FeatureFlag) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FeatureFlag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", environment=" + environment +
                '}';
    }
}
