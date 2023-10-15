package com.trainitek.backtothefuture.domain;

import com.trainitek.backtothefuture.domain.base.UuidAggregateRoot;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class Course extends UuidAggregateRoot {

    @Getter
    private String courseName;

    @Builder
    private Course(@NonNull String courseName) {
        this.courseName = courseName;
    }
}
