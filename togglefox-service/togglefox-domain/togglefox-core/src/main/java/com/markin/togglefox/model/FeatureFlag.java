package com.markin.togglefox.model;

import com.markin.togglefox.event.*;
import com.markin.togglefox.strategy.RolloutStrategy;
import com.markin.togglefox.valueobject.Environment;
import com.markin.togglefox.valueobject.EvaluationContext;
import com.markin.togglefox.valueobject.FeatureFlagId;
import com.markin.togglefox.valueobject.FlagEvaluationResult;

import java.time.LocalDateTime;
import java.util.*;

public class FeatureFlag {
    private final FeatureFlagId id;
    private String name;
    private String description;
    private boolean enabled;
    private Environment environment;
    private RolloutStrategy rolloutStrategy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private Set<String> tags;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private FeatureFlag(FeatureFlagId id, String name, String description,
                        Environment environment, String createdBy) {
        this.id = Objects.requireNonNull(id, "Feature flag ID cannot be null");
        this.name = validateName(name);
        this.description = description;
        this.environment = Objects.requireNonNull(environment, "Environment cannot be null");
        this.enabled = false;
        this.createdBy = Objects.requireNonNull(createdBy, "Creator cannot be null");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.tags = new HashSet<>();

        addDomainEvent(new FeatureFlagCreatedEvent(this.id, this.name, this.environment));
    }

    public static FeatureFlag create(String name, String description,
                                     Environment environment, String createdBy) {
        return new FeatureFlag(FeatureFlagId.generate(), name, description, environment, createdBy);
    }

    public static FeatureFlag reconstitute(FeatureFlagId id, String name, String description,
                                           boolean enabled, Environment environment,
                                           RolloutStrategy rolloutStrategy, LocalDateTime createdAt,
                                           LocalDateTime updatedAt, String createdBy, Set<String> tags) {
        FeatureFlag flag = new FeatureFlag(id, name, description, environment, createdBy);
        flag.enabled = enabled;
        flag.rolloutStrategy = rolloutStrategy;
        flag.createdAt = createdAt;
        flag.updatedAt = updatedAt;
        flag.tags = new HashSet<>(tags);
        flag.domainEvents.clear();
        return flag;
    }

    /**
     * Core business logic: Evaluate if the feature should be enabled for a given context
     */
    public FlagEvaluationResult evaluate(EvaluationContext context) {
        // If flag is globally disabled, return false immediately
        if (!this.enabled) {
            return FlagEvaluationResult.disabled("Feature flag is globally disabled");
        }

        // Environment must match
        if (!this.environment.equals(context.getEnvironment())) {
            return FlagEvaluationResult.disabled("Environment mismatch");
        }

        // Apply rollout strategy if present
        if (this.rolloutStrategy != null) {
            return this.rolloutStrategy.evaluate(context);
        }

        // Default: enabled for all users if no strategy is defined
        return FlagEvaluationResult.enabled("No rollout strategy - enabled for all");
    }

    /**
     * Enable the feature flag
     */
    public void enable() {
        if (!this.enabled) {
            this.enabled = true;
            this.updatedAt = LocalDateTime.now();
            addDomainEvent(new FeatureFlagEnabledEvent(this.id, this.name));
        }
    }

    /**
     * Disable the feature flag
     */
    public void disable() {
        if (this.enabled) {
            this.enabled = false;
            this.updatedAt = LocalDateTime.now();
            addDomainEvent(new FeatureFlagDisabledEvent(this.id, this.name));
        }
    }

    /**
     * Update rollout strategy
     */
    public void updateRolloutStrategy(RolloutStrategy strategy) {
        this.rolloutStrategy = strategy;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(new FeatureFlagStrategyUpdatedEvent(this.id, this.name, strategy.getType()));
    }

    /**
     * Add tags for organization
     */
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag.trim().toLowerCase());
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove tag
     */
    public void removeTag(String tag) {
        if (this.tags.remove(tag)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Domain event management
    private void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Validation
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Feature flag name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Feature flag name cannot exceed 100 characters");
        }
        return name.trim();
    }

    // Getters
    public FeatureFlagId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public RolloutStrategy getRolloutStrategy() {
        return rolloutStrategy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

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
