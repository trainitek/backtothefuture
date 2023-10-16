package com.trainitek.backtothefuture.domain;

import com.trainitek.backtothefuture.domain.base.UuidAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class Enrollment extends UuidAggregateRoot {

    @ManyToOne
    User student;

    @ManyToOne
    User enroller;

    @ManyToOne
    Course course;

    @Getter
    Instant availableFrom;

    @Getter
    Instant enrolledAt;

    @Getter
    Instant startedAt;

    @Getter
    Instant completedAt;

    @Getter
    Instant completionValidUntil;

    static final int COMPLETION_VALID_DURATION = 24;

    @Builder
    private Enrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                       @NonNull Instant enrolledAt, @NonNull Instant availableFrom,
                       Instant startedAt, Instant completedAt) {
        this.student = student;
        this.enroller = enroller;
        this.course = course;
        this.enrolledAt = enrolledAt;
        this.availableFrom = availableFrom;
        if (this.enrolledAt.isAfter(this.availableFrom)) {
            throw new IllegalArgumentException(
                    "Enrolled date %s should be before available date %s."
                            .formatted(this.enrolledAt, this.availableFrom)
            );
        }
        if (startedAt != null) {
            throwIfStartDateIsBeforeAvailableFrom(startedAt);
            this.startedAt = startedAt;
        }
        if (completedAt != null) {
            throwIfNotStarted();
            throwIfStartDateIsAfterCompletionDate(completedAt);
            this.completedAt = completedAt;
        }
    }

    public static Enrollment initialEnrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                                               @NonNull Instant enrolledAt,
                                               @NonNull Instant availableFrom) {
        return new Enrollment(student, enroller, course, enrolledAt, availableFrom, null, null);
    }

    public static Enrollment startedEnrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                                               @NonNull Instant enrolledAt, @NonNull Instant availableFrom,
                                               @NonNull Instant startedAt) {
        return new Enrollment(student, enroller, course, enrolledAt, availableFrom, startedAt, null);
    }

    public static Enrollment completedEnrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                                                 @NonNull Instant enrolledAt,
                                                 @NonNull Instant availableFrom,
                                                 @NonNull Instant startedAt,
                                                 @NonNull Instant completedAt) {
        return new Enrollment(student, enroller, course, enrolledAt, availableFrom, startedAt, completedAt);
    }

    public void startAt(Clock clock) {
        var startedAt = Instant.now(clock);
        throwIfStartDateIsBeforeAvailableFrom(startedAt);
        this.startedAt = startedAt;
    }

    public void completeAt(Clock completedAt) {
        throwIfNotStarted();
        Instant completedAtDateTime = Instant.now(completedAt);
        throwIfStartDateIsAfterCompletionDate(completedAtDateTime);
        this.completedAt = completedAtDateTime;
        this.completionValidUntil = calculateValidUntilDate(completedAt);
    }

    private Instant calculateValidUntilDate(Clock completedAt) {
        ZoneId zone = completedAt.getZone();
        return completedAt.instant()
                .atZone(zone)
                .plusMonths(COMPLETION_VALID_DURATION)
                .toInstant();
    }

    private void throwIfStartDateIsBeforeAvailableFrom(Instant startedAt) {
        if (startedAt.isBefore(this.availableFrom)) {
            throw new IllegalArgumentException(
                    "Cannot start the enrollment. Current date %s is before available date %s."
                            .formatted(startedAt, this.availableFrom)
            );
        }
    }

    private void throwIfNotStarted() {
        if (!isStarted()) {
            throw new IllegalArgumentException("Cannot complete an enrollment that hasn't started.");
        }
    }

    private void throwIfStartDateIsAfterCompletionDate(Instant completedAt) {
        if (this.startedAt.isAfter(completedAt)) {
            throw new IllegalArgumentException(
                    "Cannot complete the enrollment. Started at date %s is after current date (completion date) %s."
                            .formatted(this.startedAt, completedAt)
            );
        }
    }

    public boolean isStarted() {
        return this.startedAt != null;
    }

    public boolean isCompleted() {
        return this.completedAt != null;
    }
}
