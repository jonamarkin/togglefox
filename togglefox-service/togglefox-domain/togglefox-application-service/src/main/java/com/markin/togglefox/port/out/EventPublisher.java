package com.markin.togglefox.port.out;

import com.markin.togglefox.domain.event.DomainEvent;

public interface EventPublisher {
    /**
     * Publish a domain event
     */
    void publish(DomainEvent event);

}
