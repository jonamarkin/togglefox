package com.markin.togglefox.domain.event;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.strategy.RolloutStrategy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class FeatureFlagStrategyUpdatedEvent implements DomainEvent {

    private final String eventId;
    private final FeatureFlagId flagId;
    private final String flagName;
    private final RolloutStrategy oldStrategy;
    private final RolloutStrategy newStrategy;
    private final LocalDateTime occurredAt;

    public FeatureFlagStrategyUpdatedEvent(FeatureFlagId flagId, String flagName,
                                           RolloutStrategy oldStrategy, RolloutStrategy newStrategy) {
        this.eventId = UUID.randomUUID().toString();
        this.flagId = Objects.requireNonNull(flagId, "Flag ID cannot be null");
        this.flagName = Objects.requireNonNull(flagName, "Flag name cannot be null");
        this.oldStrategy = Objects.requireNonNull(oldStrategy, "Old strategy cannot be null");
        this.newStrategy = Objects.requireNonNull(newStrategy, "New strategy cannot be null");
        this.occurredAt = LocalDateTime.now();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "FeatureFlagStrategyUpdated";
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public FeatureFlagId getFlagId() {
        return flagId;
    }

    public String getFlagName() {
        return flagName;
    }

    public RolloutStrategy getOldStrategy() {
        return oldStrategy;
    }

    public RolloutStrategy getNewStrategy() {
        return newStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureFlagStrategyUpdatedEvent that = (FeatureFlagStrategyUpdatedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "FeatureFlagStrategyUpdatedEvent{" +
                "eventId='" + eventId + '\'' +
                ", flagId=" + flagId +
                ", flagName='" + flagName + '\'' +
                ", oldStrategy=" + oldStrategy +
                ", newStrategy=" + newStrategy +
                ", occurredAt=" + occurredAt +
                '}';
    }
}