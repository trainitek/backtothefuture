package com.trainitek.backtothefuture.domain.base;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.Clock;
import java.time.Instant;

import static java.util.UUID.randomUUID;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor(access = PROTECTED)
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
@ToString
public class ClassBasedMetaData implements DomainEvent.MetaData {

    @NonNull String eventId;
    @NonNull String eventType;
    @NonNull Instant eventTimestamp;

    public static ClassBasedMetaData metaData(@NonNull Class<? extends DomainEvent> eventClass, @NonNull Clock clock) {
        return new ClassBasedMetaData(randomUUID().toString(), eventClass.getSimpleName(), clock.instant());
    }
}
