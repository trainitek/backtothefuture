package com.trainitek.backtothefuture.infrastructure.spring;

import com.trainitek.backtothefuture.domain.base.DomainEvent;
import com.trainitek.backtothefuture.domain.base.DomainEventPublisher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
@Slf4j
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(@NonNull DomainEvent event) {
        log.info("--- Domain event: {}", event);
        eventPublisher.publishEvent(event);
    }
}
