package com.markin.togglefox.domain.event;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlagId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class FeatureFlagDisabledEvent implements DomainEvent {

    private final String eventId;
    private final FeatureFlagId flagId;
    private final String flagName;
    private final LocalDateTime occurredAt;

    public FeatureFlagDisabledEvent(FeatureFlagId flagId, String flagName) {
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
        return "FeatureFlagDisabled";
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
        FeatureFlagDisabledEvent that = (FeatureFlagDisabledEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "FeatureFlagDisabledEvent{" +
                "eventId='" + eventId + '\'' +
                ", flagId=" + flagId +
                ", flagName='" + flagName + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}