package com.markin.togglefox.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    /**
     * Unique identifier for this event occurrence
     */
    String getEventId();

    /**
     * Type of the event (used for routing and handling)
     */
    String getEventType();

    /**
     * When the event occurred
     */
    LocalDateTime getOccurredAt();

    /**
     * Version of the event schema (for evolution)
     */
    default int getVersion() {
        return 1;
    }
}
