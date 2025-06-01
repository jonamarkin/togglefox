package com.markin.togglefox.port.out;

import com.markin.togglefox.event.DomainEvent;

import java.util.List;

public interface EventPublisherPort {
    void publishEvent(DomainEvent event);
    void publishEvents(List<DomainEvent> events);

}
