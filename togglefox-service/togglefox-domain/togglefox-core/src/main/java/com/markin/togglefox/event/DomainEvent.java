package com.markin.togglefox.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getOccurredOn();
    String getEventType();
}
