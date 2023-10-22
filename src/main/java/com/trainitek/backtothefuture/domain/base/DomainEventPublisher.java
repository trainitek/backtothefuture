package com.trainitek.backtothefuture.domain.base;

import lombok.NonNull;

public interface DomainEventPublisher {
    void publish(@NonNull DomainEvent event);
}
