package com.trainitek.backtothefuture.domain.base;

import lombok.NonNull;

import java.util.List;

/**
 * Generic Aggregate Root interface.
 *
 * @param <ID> type of the aggregate identifier.
 */
public interface AggregateRoot<ID> {

    ID getId();

    List<DomainEvent> getUncommittedEvents();

    /**
     * Publishes all uncommitted events in this aggregate using the specified {@link DomainEventPublisher}.
     *
     * @see AggregateRoot#getUncommittedEvents()
     */
    void publishUncommittedEventsUsing(@NonNull DomainEventPublisher publisher);
}
