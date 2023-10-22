package com.trainitek.backtothefuture.domain.base;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@MappedSuperclass
public abstract class JpaAggregateRoot<ID> implements AggregateRoot<ID> {

    /**
     * A version property cannot be of primitive type.
     * This is because of Hibernate that accepts only NULL values as versions of *new* entities with pre-populated IDs.
     */
    @Version
    @Getter
    private Integer version;

    @Transient
    private final List<DomainEvent> events = new ArrayList<>();

    @Override
    public List<DomainEvent> getUncommittedEvents() {
        return List.copyOf(events);
    }

    @Override
    public void publishUncommittedEventsUsing(@NonNull DomainEventPublisher publisher) {
        var copy = getUncommittedEvents();
        this.events.clear();
        copy.forEach(publisher::publish);
    }

    protected void addEvent(@NonNull DomainEvent event) {
        events.add(event);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AggregateRoot)) {
            return false;
        }
        checkState(getId() != null, "Id must not be null");
        var otherId = ((AggregateRoot<?>) obj).getId();
        checkArgument(otherId != null, "Id of comparing object cannot be null");
        return getId().equals(otherId);
    }

    @Override
    public int hashCode() {
        checkState(getId() != null, "Id must not be null");
        return getId().hashCode();
    }
}
