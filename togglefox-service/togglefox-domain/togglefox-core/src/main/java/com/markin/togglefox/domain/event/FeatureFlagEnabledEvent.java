package com.markin.togglefox.domain.event;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class FeatureFlagEnabledEvent implements DomainEvent {

    private final String eventId;
    private final FeatureFlagId flagId;
    private final String flagName;
    private final LocalDateTime occurredAt;

    public FeatureFlagEnabledEvent(FeatureFlagId flagId, String flagName) {
        this.eventId = UUID.randomUUID().toString();
        this.flagId = Objects.requireNonNull(flagId, "Flag ID cannot be null");
        this.flagName = Objects.requireNonNull(flagName, "Flag name cannot be null");
        this.occurredAt = LocalDateTime.now();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "FeatureFlagEnabled";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureFlagEnabledEvent that = (FeatureFlagEnabledEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "FeatureFlagEnabledEvent{" +
                "eventId='" + eventId + '\'' +
                ", flagId=" + flagId +
                ", flagName='" + flagName + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}