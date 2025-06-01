package com.markin.togglefox.port.out;

import com.markin.togglefox.event.DomainEvent;

import java.util.List;

public interface EventPublisher {
    /**
     * Publish a single domain event
     */
    void publish(DomainEvent event);

    /**
     * Publish multiple domain events atomically
     */
    void publishAll(Iterable<DomainEvent> events);

    /**
     * Publish event asynchronously
     */
    void publishAsync(DomainEvent event);

    /**
     * Publish multiple events asynchronously
     */
    void publishAllAsync(Iterable<DomainEvent> events);

}
