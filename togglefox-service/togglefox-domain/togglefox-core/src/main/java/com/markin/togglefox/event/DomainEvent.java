package com.markin.togglefox.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    /**
     * Unique identifier for this event occurrence
     */
    UUID getEventId();

    /**
     * When this event occurred
     */
    LocalDateTime getOccurredAt();

    /**
     * Type of event (for serialization and routing)
     */
    String getEventType();

    /**
     * Version of the event schema (for evolution)
     */
    int getVersion();

    /**
     * ID of the aggregate that generated this event
     */
    String getAggregateId();

    /**
     * Type of the aggregate that generated this event
     */
    String getAggregateType();

    /**
     * User or system that triggered this event
     */
    String getTriggeredBy();
}
