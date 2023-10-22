package com.trainitek.backtothefuture.domain.base;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public abstract class UuidAggregateRoot extends JpaAggregateRoot<UUID> {

    @Id
    @Getter
    @ToString.Include
    private UUID id;

    protected UuidAggregateRoot() {
        id = randomUUID();
    }

    protected UuidAggregateRoot(@NonNull UUID id) {
        this.id = id;
    }
}
