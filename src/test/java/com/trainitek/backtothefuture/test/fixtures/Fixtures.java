package com.trainitek.backtothefuture.test.fixtures;

import com.trainitek.backtothefuture.domain.Course;
import com.trainitek.backtothefuture.domain.Enrollment;
import com.trainitek.backtothefuture.domain.User;
import lombok.NonNull;

import java.time.Clock;
import java.time.Instant;

public class Fixtures {
    public static User someStudent() {
        return User.builder().firstName("John").lastName("Doe").build();
    }

    public static Course someCourse() {
        return Course.builder().courseName("Trainitek - Implementing Modern Architecture").build();
    }

    public static Enrollment.EnrollmentBuilder startedEnrollment(@NonNull Instant enrolledAt,
                                                                 @NonNull Instant availableFrom,
                                                                 @NonNull Instant startedAt,
                                                                 @NonNull Clock clock) {
        return Enrollment.builder()
                .course(someCourse())
                .student(someStudent())
                .enroller(someStudent())
                .enrolledAt(enrolledAt)
                .availableFrom(availableFrom)
                .startedAt(startedAt)
                .clock(clock);
    }

    public static Enrollment.EnrollmentBuilder initialEnrollment(@NonNull Instant enrolledAt,
                                                                 @NonNull Instant availableFrom,
                                                                 @NonNull Clock clock) {
        return Enrollment.builder()
                .course(someCourse())
                .student(someStudent())
                .enroller(someStudent())
                .enrolledAt(enrolledAt)
                .availableFrom(availableFrom)
                .clock(clock);
    }

    public static Enrollment someInitialEnrollment(@NonNull Instant enrolledAt,
                                                   @NonNull Instant availableFrom,
                                                   @NonNull Clock clock) {
        return initialEnrollment(enrolledAt, availableFrom, clock).build();
    }

    public static Enrollment someStartedEnrollment(@NonNull Instant enrolledAt,
                                                   @NonNull Instant availableFrom,
                                                   @NonNull Instant startedAt,
                                                   @NonNull Clock clock) {
        return startedEnrollment(enrolledAt, availableFrom, startedAt, clock).build();
    }

}
