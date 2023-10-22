package com.trainitek.backtothefuture.domain.base;

import java.time.Instant;

public interface DomainEvent {
    MetaData metaData();

    /**
     * Technical / audit information included into a domain event, e.g. when the event occurred.
     * Implementations may contain additional data if needed, for example, a username.<br/>
     */
    interface MetaData {
        String eventId();

        String eventType();

        Instant eventTimestamp();
    }
}
