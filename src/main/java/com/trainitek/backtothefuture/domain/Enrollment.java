package com.trainitek.backtothefuture.domain;

import com.trainitek.backtothefuture.domain.base.UuidAggregateRoot;
import com.trainitek.backtothefuture.domain.events.EnrollmentCompleted;
import com.trainitek.backtothefuture.domain.events.EnrollmentStarted;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Clock;
import java.time.Instant;

import static com.trainitek.backtothefuture.domain.base.ClassBasedMetaData.metaData;
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
                       Instant startedAt, Instant completedAt, @NonNull Clock clock) {
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
            doStartAt(startedAt, clock);
        }
        if (completedAt != null) {
            throwIfNotStarted();
            throwIfStartDateIsAfterCompletionDate(completedAt);
            doCompleteAt(completedAt, clock);
        }
    }

    public static Enrollment initialEnrollment(@NonNull User student, @NonNull User enroller, @NonNull Course course,
                                               @NonNull Instant enrolledAt,
                                               @NonNull Instant availableFrom,
                                               @NonNull Clock clock) {
        return new Enrollment(student, enroller, course, enrolledAt, availableFrom, null, null,
                clock);
    }

    public void startAt(Clock clock) {
        var startedAt = Instant.now(clock);
        throwIfStartDateIsBeforeAvailableFrom(startedAt);
        doStartAt(startedAt, clock);
    }

    private void doStartAt(@NonNull Instant startedAt, @NonNull Clock clock) {
        this.startedAt = startedAt;
        addEvent(new EnrollmentStarted(getId(), startedAt, metaData(EnrollmentStarted.class, clock)));
    }

    public void completeAt(Clock completedAt) {
        throwIfNotStarted();
        var completedAtDateTime = Instant.now(completedAt);
        throwIfStartDateIsAfterCompletionDate(completedAtDateTime);
        doCompleteAt(completedAtDateTime, completedAt);
    }

    private void doCompleteAt(@NonNull Instant completedAt, @NonNull Clock clock) {
        this.completedAt = completedAt;
        this.completionValidUntil = calculateValidUntilDate(completedAt, clock);
        addEvent(new EnrollmentCompleted(getId(), completedAt, metaData(EnrollmentCompleted.class, clock)));
    }

    private Instant calculateValidUntilDate(@NonNull Instant completedAt, @NonNull Clock clock) {
        return completedAt
                .atZone(clock.getZone())
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
