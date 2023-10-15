package com.trainitek.backtothefuture.domain;

import com.trainitek.backtothefuture.domain.base.UuidAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Period;

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

    LocalDateTime availableFrom;

    LocalDateTime enrolledAt;

    LocalDateTime startedAt;

    LocalDateTime completedAt;

    LocalDateTime completionValidUntil;

    public static final Period COMPLETION_VALID_DURATION = Period.ofMonths(25);

    @Builder
    private Enrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                       @NonNull LocalDateTime enrolledAt, @NonNull LocalDateTime availableFrom,
                       LocalDateTime startedAt, LocalDateTime completedAt) {
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
            throwIfStartDateBeforeAvailableFrom(startedAt);
            this.startedAt = startedAt;
        }
        if (completedAt != null) {
            throwIfStartDateIsAfterCompletionDate(completedAt);
            this.completedAt = completedAt;
        }
    }

    public static Enrollment initialEnrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                                               @NonNull LocalDateTime enrolledAt,
                                               @NonNull LocalDateTime availableFrom) {
        return new Enrollment(student, enroller, course, enrolledAt, availableFrom, null, null);
    }

    public static Enrollment startedEnrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                                               @NonNull LocalDateTime enrolledAt, @NonNull LocalDateTime availableFrom,
                                               @NonNull LocalDateTime startedAt) {
        return new Enrollment(student, enroller, course, enrolledAt, availableFrom, startedAt, null);
    }

    public static Enrollment completedEnrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                                                 @NonNull LocalDateTime enrolledAt,
                                                 @NonNull LocalDateTime availableFrom,
                                                 @NonNull LocalDateTime startedAt,
                                                 @NonNull LocalDateTime completedAt) {
        return new Enrollment(student, enroller, course, enrolledAt, availableFrom, startedAt, completedAt);
    }

    public void startAt(Clock clock) {
        var startedAt = LocalDateTime.now(clock);
        throwIfStartDateBeforeAvailableFrom(startedAt);
        this.startedAt = startedAt;
    }

    private void throwIfStartDateBeforeAvailableFrom(LocalDateTime startedAt) {
        if (startedAt.isBefore(this.availableFrom)) {
            throw new IllegalArgumentException(
                    "Cannot start the enrollment. Current date %s is before available date %s."
                            .formatted(startedAt, this.availableFrom)
            );
        }
    }

    private void throwIfStartDateIsAfterCompletionDate(LocalDateTime completedAt) {
        if (this.startedAt.isAfter(completedAt)) {
            throw new IllegalArgumentException(
                    "Cannot complete the enrollment. Started at date %s is after current date (completion date) %s."
                            .formatted(this.startedAt, completedAt)
            );
        }
    }

    public void completeAt(Clock completedAt) {
        if (!isStarted()) {
            throw new IllegalArgumentException("Cannot complete an enrollment that hasn't started.");
        }
        LocalDateTime completedAtDateTime = LocalDateTime.now(completedAt);
        throwIfStartDateIsAfterCompletionDate(completedAtDateTime);
        this.completedAt = completedAtDateTime;
        this.completionValidUntil = this.completedAt.plus(COMPLETION_VALID_DURATION);
    }

    public boolean isStarted() {
        return this.startedAt != null;
    }

    public boolean isCompleted() {
        return this.completedAt != null;
    }
}
