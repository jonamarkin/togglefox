package com.markin.togglefox.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractDomainEvent implements DomainEvent {
    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String triggeredBy;

    protected AbstractDomainEvent(String aggregateId, String aggregateType, String triggeredBy) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.aggregateId = Objects.requireNonNull(aggregateId, "Aggregate ID cannot be null");
        this.aggregateType = Objects.requireNonNull(aggregateType, "Aggregate type cannot be null");
        this.triggeredBy = Objects.requireNonNull(triggeredBy, "Triggered by cannot be null");
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getAggregateType() {
        return aggregateType;
    }

    @Override
    public String getTriggeredBy() {
        return triggeredBy;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDomainEvent that = (AbstractDomainEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "eventId=" + eventId +
                ", occurredAt=" + occurredAt +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", triggeredBy='" + triggeredBy + '\'' +
                '}';
    }
}
