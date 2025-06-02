package com.markin.togglefox.dataaccess.messaging;

import com.markin.togglefox.domain.event.DomainEvent;
import com.markin.togglefox.port.out.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(DomainEventPublisher.class);

    @Override
    public void publish(DomainEvent event) {
        //Log the event details for now
        logger.info("Publishing domain event: {} - {}",
                event.getEventType(),
                event.getEventId());

        /**
         * TODO: Implement actual event publishing logic with either RabbitMQ, Kafka, or any other messaging system.
         */
    }
}