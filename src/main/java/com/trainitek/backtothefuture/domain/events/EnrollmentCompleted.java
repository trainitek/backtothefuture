package com.trainitek.backtothefuture.domain.events;

import com.trainitek.backtothefuture.domain.base.DomainEvent;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public record EnrollmentCompleted(@NonNull UUID enrollmentId,
                                  @NonNull Instant completedAt,
                                  @NonNull MetaData metaData) implements DomainEvent {
}
